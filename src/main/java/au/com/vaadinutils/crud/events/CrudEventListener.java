package au.com.vaadinutils.crud.events;

import au.com.vaadinutils.crud.CrudEntity;

/**
 * @deprecated Will be removed once dependent classes are removed.
 */
public interface CrudEventListener {
    // Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public void crudEvent(CrudEventType event, CrudEntity entity);
}
