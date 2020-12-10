package au.com.vaadinutils.crud;

/**
 * TODO LC: Check if this is still needed, or simply using CrudEntity is
 * sufficient.
 */
public interface ChildCrudEntity extends CrudEntity {
    /**
     * This is so the child crud can select the same record after saving one or more
     * new records
     * 
     * as the id doesn't exist before the entity is persisted and the child crud has
     * possibly multiple records uncommitted it needs a "GUID" that is created when
     * the entity is instantiated so that it can locate the record again after being
     * persisted.
     * 
     * recommended implementation looks like this...
     * 
     * <pre>
     * <code>
     * 
     * {@literal @}NotNull
     * {@literal @}Column(updatable = false) 
     * String guid = JpaEntityHelper.getGuid(this);
     * 
     * {@literal @}Override public String getGuid() 
     * { 
     * 		return guid; 
     * }
     * </code>
     * </pre>
     * 
     */
    String getGuid();
}
