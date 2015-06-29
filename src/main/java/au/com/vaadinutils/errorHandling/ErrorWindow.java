package au.com.vaadinutils.errorHandling;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class ErrorWindow
{
	private static final Long MAX_ATTACHMENT_SIZE = new Long(2000000); // 2MB
	private Button close = new Button("OK");
	private Label uploadStatus = new Label("&nbsp;", ContentMode.HTML);

	static Logger logger = LogManager.getLogger();

	public ErrorWindow()
	{
		// Configure the error handler for the UI
		UI.getCurrent().setErrorHandler(new DefaultErrorHandler()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void error(com.vaadin.server.ErrorEvent event)
			{
				internalShowErrorWindow(event.getThrowable());
			}

		});
	}

	public static void showErrorWindow(Throwable error)
	{
		new ErrorWindow().internalShowErrorWindow(error);
		;
	}

	private void internalShowErrorWindow(Throwable error)
	{

		try
		{
			ViolationConstraintHandler.handleConstraintViolationException(error);
		}
		catch (Throwable e)
		{
			error = e;
		}

		// Find the final cause
		String fullTrace = "";

		String causeClass = "";
		String id = "";

		final Date time = new Date();
		Throwable cause = null;
		for (Throwable t = error; t != null; t = t.getCause())
		{
			if (t.getCause() == null) // We're at final cause
				cause = t;
			fullTrace += t.getClass().getCanonicalName() + " " + t.getMessage() + "\n";
			for (StackTraceElement trace : t.getStackTrace())
			{
				fullTrace += "at " + trace.getClassName() + "." + trace.getMethodName() + "(" + trace.getFileName()
						+ ":" + trace.getLineNumber() + ")\n";
			}
			fullTrace += "\n\n";
		}
		if (cause != null)
		{
			causeClass = cause.getClass().getSimpleName();

			id = cause.getClass().getCanonicalName() + "\n";
			for (StackTraceElement trace : cause.getStackTrace())
			{
				id += "at " + trace.getClassName() + "." + trace.getMethodName() + "(" + trace.getFileName() + ":"
						+ trace.getLineNumber() + ")\n";
			}
			id = "" + id.hashCode();
		}

		final String finalId = id;
		final String finalTrace = fullTrace;
		// final Throwable finalCause = cause;
		final String reference = UUID.randomUUID().toString();

		logger.error("Reference: " + reference + " Version: " + getBuildVersion() + " System: " + getSystemName() + " "
				+ error, error);
		logger.error("Reference: " + reference + " " + cause, cause);

		if (UI.getCurrent() != null)
		{
			displayVaadinErrorWindow(causeClass, id, time, finalId, finalTrace, reference);
		}
	}

	private void displayVaadinErrorWindow(String causeClass, String id, final Date time, final String finalId,
			final String finalTrace, final String reference)
	{
		final Window window = new Window();
		UI.getCurrent().addWindow(window);
		window.setModal(true);
		window.center();
		window.setResizable(false);
		window.setCaption("Error " + id);
		window.setClosable(false);

		// window.setHeight("50%");
		window.setWidth("50%");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		final Label message = new Label("<b>An error has occurred (" + causeClass + ").<br><br>Reference:</b> "
				+ reference);
		message.setContentMode(ContentMode.HTML);

		Label describe = new Label("<b>Please describe what you were doing when this error occured (Optional)<b>");
		describe.setContentMode(ContentMode.HTML);

		final TextArea notes = new TextArea();
		notes.setWidth("100%");
		final String supportEmail = getTargetEmailAddress();

		close.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				try
				{
					logger.error("Reference: " + reference + " " + notes.getValue());

					String subject = "";
					String companyName = getSystemName();
					subject += "Error: " + finalId + " " + companyName + " ref: " + reference;

					String viewClass = getViewName();

					ErrorSettingsFactory.getErrorSettings().sendEmail(
							supportEmail,
							subject,
							subject + "\n\nTime: " + time.toString() + "\n\nView: " + viewClass + "\n\nUser: "
									+ getUserName() + "\n\n" + "Version: " + getBuildVersion() + "\n\n" + "User notes:"
									+ notes.getValue() + "\n\n" + finalTrace, ErrorWindow.this.stream,
							ErrorWindow.this.filename, ErrorWindow.this.MIMEType);
				}
				catch (Exception e)
				{
					logger.error(e, e);
					Notification.show("Error sending error report", Type.ERROR_MESSAGE);
				}
				finally
				{
					window.close();
				}
			}

		});
		close.setStyleName(ValoTheme.BUTTON_DANGER);

		Label printMessage = new Label("<font color='red'>Taking a screen shot and sending it to " + supportEmail
				+ " will help with diagnosing the problem</font>");
		printMessage.setContentMode(ContentMode.HTML);

		layout.addComponent(message);
		layout.addComponent(describe);
		layout.addComponent(notes);
		layout.addComponent(createAttachmentComponent());
		layout.addComponent(uploadStatus);
		layout.addComponent(close);
		layout.addComponent(new Label("Information about this error will be sent to " + getSupportCompanyName()));
		window.setContent(layout);
		// Display the error message in a custom fashion

		// Do the default error handling (optional)
		// doDefault(event);
	}

	private ByteArrayOutputStream stream = null;
	private String filename;
	private String MIMEType;
	private boolean attachmentTooLarge;

	@SuppressWarnings("serial")
	private Upload createAttachmentComponent()
	{
		final Receiver receiver = new Receiver()
		{

			private static final long serialVersionUID = 3413693084667621411L;

			@Override
			public OutputStream receiveUpload(String filename, String MIMEType)
			{
				ErrorWindow.this.stream = new ByteArrayOutputStream();
				ErrorWindow.this.filename = filename;
				ErrorWindow.this.MIMEType = MIMEType;
				return ErrorWindow.this.stream;
			}
		};

		final Upload upload = new Upload(
				"Taking a screenshot and attaching it will help with diagnosing the problem (Optional - Maximum 2MB)",
				receiver);
		upload.setButtonCaption("Upload Attachment");
		upload.setImmediate(true);

		final SucceededListener succeededListener = new SucceededListener()
		{

			@Override
			public void uploadSucceeded(SucceededEvent event)
			{
				ErrorWindow.this.setUploadStatus("Uploaded attachment: " + event.getFilename(), false);
				close.setEnabled(true);
			}
		};
		upload.addSucceededListener(succeededListener);

		final FailedListener failedListener = new FailedListener()
		{

			@Override
			public void uploadFailed(FailedEvent event)
			{
				if (attachmentTooLarge)
				{
					attachmentTooLarge = false;
					ErrorWindow.this.setUploadStatus("Attachment is too large. Maximum size is 2MB.", true);
				}
				else
				{
					ErrorWindow.this.setUploadStatus("Failed to upload attachment: " + event.getFilename(), true);
					logger.error(event.getReason(), event.getReason());
				}
				close.setEnabled(true);
			}
		};
		upload.addFailedListener(failedListener);

		final StartedListener startedListener = new StartedListener()
		{

			@Override
			public void uploadStarted(StartedEvent event)
			{
				close.setEnabled(false);
				attachmentTooLarge = false;
				if (event.getContentLength() > MAX_ATTACHMENT_SIZE)
				{
					attachmentTooLarge = true;
					upload.interruptUpload();
				}
				else
				{
					ErrorWindow.this.setUploadStatus("Uploading...", false);
				}
			}
		};
		upload.addStartedListener(startedListener);

		return upload;
	}

	private void setUploadStatus(String message, boolean error)
	{
		// Prevent component collapsing
		if (message.isEmpty())
		{
			message = "&nbsp;";
		}
		if (error)
		{
			message = "<font color='red'>" + message + "</font>";
		}
		uploadStatus.setValue(message);
	}

	private static String getViewName()
	{
		try
		{
			return ErrorSettingsFactory.getErrorSettings().getViewName();
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return "Error getting View name";
	}

	private static String getSupportCompanyName()
	{
		try
		{
			return ErrorSettingsFactory.getErrorSettings().getSupportCompanyName();
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return "Error getting Support Company Name";
	}

	private static String getTargetEmailAddress()
	{
		try
		{
			return ErrorSettingsFactory.getErrorSettings().getTargetEmailAddress();
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return "Error getting Target Email Address";
	}

	private static String getBuildVersion()
	{
		try
		{
			return ErrorSettingsFactory.getErrorSettings().getBuildVersion();
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return "Error getting build Version";
	}

	private static String getUserName()
	{
		try
		{
			return ErrorSettingsFactory.getErrorSettings().getUserName();
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return "Error getting user name";
	}

	private static String getSystemName()
	{
		try
		{
			return ErrorSettingsFactory.getErrorSettings().getSystemName();
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return "Error getting System name";
	}

}
