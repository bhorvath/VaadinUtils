package au.com.vaadinutils.dao;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @deprecated Not used?
 */
public class JpaEntityHelper
{

	final static AtomicInteger seeds = new AtomicInteger();

	public static String getGuid(Object clazz)
	{
		return getGuid();
	}

	public static String getGuid()
	{
		long stamp = System.currentTimeMillis();
		long id = seeds.incrementAndGet();

		int current = seeds.get();
		if (current > 100000)
		{
			seeds.compareAndSet(current, 0);
		}
		return (stamp + "-" + id);

	}

}
