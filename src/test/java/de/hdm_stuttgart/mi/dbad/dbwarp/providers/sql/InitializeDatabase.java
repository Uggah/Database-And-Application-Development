package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InitializeDatabase {

  String value() default "";

}
