package au.com.vaadinutils.jasper.scheduler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;

public class DayOfWeekCheckBoxes extends HorizontalLayout implements Field<String>
{
	// Logger logger = LogManager.getLogger();

	private static final long serialVersionUID = -3339061540299550077L;

	private Property<String> datasource;

	List<CheckBox> boxes = new LinkedList<CheckBox>();

	private boolean required;

	public DayOfWeekCheckBoxes()
	{
		setSpacing(true);
		setSizeFull();
		String days1[] = new String[] { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
		int ctr = 0;
		for (String day : days1)
		{
			CheckBox dayCheck = new CheckBox(day);
			dayCheck.setData(ctr++);

			addComponent(dayCheck);
			boxes.add(dayCheck);
		}
	}

	public void focus()
	{
		super.focus();
	}

	@Override
	public boolean isInvalidCommitted()
	{
		return false;
	}

	@Override
	public void setInvalidCommitted(boolean isCommitted)
	{

	}

	@Override
	public void commit() throws SourceException, InvalidValueException
	{
		datasource.setValue(getValue());

	}

	@Override
	public void discard() throws SourceException
	{
		setValue(datasource.getValue());

	}

	@Override
	public void setBuffered(boolean buffered)
	{
		

	}

	@Override
	public boolean isBuffered()
	{
		
		return false;
	}

	@Override
	public boolean isModified()
	{
		
		return false;
	}

	@Override
	public void addValidator(Validator validator)
	{
		

	}

	@Override
	public void removeValidator(Validator validator)
	{
		

	}

	@Override
	public void removeAllValidators()
	{
		

	}

	@Override
	public Collection<Validator> getValidators()
	{
		
		return null;
	}

	@Override
	public boolean isValid()
	{
		
		return false;
	}

	@Override
	public void validate() throws InvalidValueException
	{
		
		if (isVisible() && getValue().length()==0)
		{
			throw new InvalidValueException("You must select at least one day");
		}

	}

	@Override
	public boolean isInvalidAllowed()
	{
		return false;
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException
	{

	}

	@Override
	public String getValue()
	{
		String value = "";
		int i = 0;
		for (CheckBox box : boxes)
		{
			if (box.getValue())
			{
				if (value.length() > 0)
				{
					value += ",";
				}
				value += i;
			}
			i++;
		}
		return value;
	}

	@Override
	public void setValue(String newValue) throws com.vaadin.data.Property.ReadOnlyException
	{
		for (CheckBox box : boxes)
		{
			box.setValue(false);
		}
		if (newValue.length() > 0)
		{
			String[] values = newValue.split(",");
			for (String value : values)
			{
				int index = Integer.parseInt(value);
				boxes.get(index).setValue(true);
			}
		}

	}

	@Override
	public Class<? extends String> getType()
	{

		return String.class;
	}

	@Override
	public void addValueChangeListener(com.vaadin.data.Property.ValueChangeListener listener)
	{
		

	}

	@Override
	public void addListener(com.vaadin.data.Property.ValueChangeListener listener)
	{
		

	}

	@Override
	public void removeValueChangeListener(com.vaadin.data.Property.ValueChangeListener listener)
	{
		

	}

	@Override
	public void removeListener(com.vaadin.data.Property.ValueChangeListener listener)
	{
		

	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
	{
		

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(@SuppressWarnings("rawtypes") Property newDataSource)
	{
		datasource = newDataSource;
		if (datasource!=null){
		setValue(datasource.getValue());}
		else 
		{
			setValue("");
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Property getPropertyDataSource()
	{
		return datasource;
	}

	@Override
	public int getTabIndex()
	{
		return 0;
	}

	@Override
	public void setTabIndex(int tabIndex)
	{

	}

	@Override
	public boolean isRequired()
	{
		return required;
	}

	@Override
	public void setRequired(boolean required)
	{
		this.required = required;

	}

	@Override
	public void setRequiredError(String requiredMessage)
	{
		

	}

	@Override
	public String getRequiredError()
	{
		return "You must select at least one day";
	}

	@Override
	public boolean isEmpty()
	{
		
		return false;
	}

	@Override
	public void clear()
	{
		
		
	}

}
