package au.com.vaadinutils.jasper.parameter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

import au.com.vaadinutils.jasper.filter.ValidateListener;
import au.com.vaadinutils.jasper.scheduler.entities.DateParameterType;

public abstract class ReportParameter<T>
{
	final Set<String> parameters = new LinkedHashSet<String>();
	final protected String label;
	protected ValidateListener validateListener;

	public ReportParameter(String label, String parameterName)
	{
		Preconditions.checkNotNull(label, "Label may not be null, as it is used for hashcode/equals");
		parameters.add(parameterName);
		this.label = label;
	}

	public ReportParameter(String label, String parameterNames[])
	{
		Preconditions.checkNotNull(label, "Label may not be null, as it is used for hashcode/equals");
		for (String param : parameterNames)
		{
			parameters.add(param);
		}
		this.label = label;
	}

	public String getUrlEncodedKeyAndParameter(String parameterName) throws UnsupportedEncodingException
	{
		return parameterName + "=" + URLEncoder.encode(getValue(parameterName).toString(), "UTF-8");
	}

	public abstract Object getValue(String parameterName);

	public abstract Component getComponent();

	public abstract boolean shouldExpand();

	public abstract void setDefaultValue(T defaultValue);

	/**
	 * 
	 * @return return a String representation of any meta data required to
	 *         rebuild this ReportParameter.
	 */
	public abstract String getSaveMetaData();

	/**
	 * reconfigure this ReportParameter using the provided (previously stored)
	 * meta data
	 */
	public abstract void applySaveMetaData(String metaData);

	/**
	 * 
	 * @return a user friendly String encompassing the meaning of the meta data
	 */
	public abstract String getMetaDataComment();

	public Set<String> getParameterNames()
	{
		return parameters;
	}

	public abstract String getExpectedParameterClassName();

	public String getLabel(String parameterName)
	{
		return label;
	}

	public boolean showFilter()
	{
		return true;
	}

	abstract public String getDisplayValue(String parameterName);

	abstract public boolean validate();

	protected boolean validateField(AbstractField<T> field)
	{
		boolean valid = false;
		try
		{

			field.setComponentError(null);
			if (validateListener != null)
			{
				validateListener.setComponentError(null);
			}
			field.validate();
			valid = true;
		}
		catch (final InvalidValueException e)
		{
			ErrorMessage componentError = new ErrorMessage()
			{

				private static final long serialVersionUID = -2976235476811651668L;

				@Override
				public String getFormattedHtmlMessage()
				{
					return e.getHtmlMessage();
				}

				@Override
				public ErrorLevel getErrorLevel()
				{
					return ErrorLevel.ERROR;
				}
			};
			field.setComponentError(componentError);
			if (validateListener != null)
			{
				validateListener.setComponentError(componentError);
			}

		}
		return valid;

	}

	public void addValidateListener(ValidateListener listener)
	{
		this.validateListener = listener;

	}

	public boolean displayInreport()
	{
		return true;
	}

	abstract public void setValueAsString(String value, String paramterName)
			throws ReadOnlyException, ConversionException, ParseException;

	abstract public boolean isDateField();

	abstract public DateParameterType getDateParameterType();

	public Date getStartDate()
	{
		throw new RuntimeException(
				"Date Parameters must overide and implement this method: " + this.getClass().getCanonicalName());
	}

	public Date getEndDate()
	{
		throw new RuntimeException(
				"Date Parameters must overide and implement this method: " + this.getClass().getCanonicalName());
	}

	public void setStartDate(Date date)
	{
		throw new RuntimeException(
				"Date Parameters must overide and implement this method: " + this.getClass().getCanonicalName());

	}

	public void setEndDate(Date date)
	{
		throw new RuntimeException(
				"Date Parameters must overide and implement this method: " + this.getClass().getCanonicalName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof ReportParameter))
		{
			return false;
		}
		ReportParameter<?> other = (ReportParameter<?>) obj;
		if (label == null)
		{
			if (other.label != null)
			{
				return false;
			}
		}
		else if (!label.equals(other.label))
		{
			return false;
		}
		return true;
	}

	public void alterParameterNames(String names)
	{
		Preconditions.checkArgument(parameters.size() == 1);
		parameters.clear();
		parameters.add(names);
	}
}
