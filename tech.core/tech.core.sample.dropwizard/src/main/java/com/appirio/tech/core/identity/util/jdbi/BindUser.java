package com.appirio.tech.core.identity.util.jdbi;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import com.appirio.tech.core.identity.model.User;

@BindingAnnotation(BindUser.UserBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindUser {

    String value() default "it";
	
	public static class UserBinderFactory implements BinderFactory {
		
		static final Logger logger = Logger.getLogger(UserBinderFactory.class);
		
		@SuppressWarnings("rawtypes")
		public Binder build(Annotation annotation) {
			return new Binder<BindUser, User>() {
				public void bind(SQLStatement stmt, BindUser bind, User obj) {
					if(obj == null)
						return;
					
					String prefix = bind.value();
					
					BeanInfo info = null;
					try {
						info = Introspector.getBeanInfo(obj.getClass());
			        } catch (Exception e) {
			        	logger.error("Failed to get information for "+obj.getClass().getName() + 
			        			": "+e.getMessage(), e);
			        }
					if(info!=null) {
			            for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
			            	try {
			            		Method reader = descriptor.getReadMethod();
			            		if(reader==null)
			            			continue;
								stmt.bind(descriptor.getName(),
										descriptor.getReadMethod().invoke(obj, new Object[0]));
							} catch (Exception e) {
					        	logger.error("Failed to get value from "+obj.getClass().getName() + 
					        			"." + descriptor.getName() + ": "+e.getMessage(), e);
							}
			            }
					}
					if(obj.getId() != null)
						stmt.bind("id", Long.parseLong(obj.getId().toString()));
					
					stmt.bind("status", obj.isActive() ? "A" : "U");
				}
			};
		}
	}
}
