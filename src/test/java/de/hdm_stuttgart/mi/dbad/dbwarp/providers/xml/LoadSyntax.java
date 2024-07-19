package de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LoadSyntax {

  /**
   * Where to load the syntax from
   */
  String value() default "";

}
