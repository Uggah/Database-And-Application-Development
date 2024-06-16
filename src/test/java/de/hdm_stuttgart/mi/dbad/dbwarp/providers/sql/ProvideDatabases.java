package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProvideDatabases {

  Class<? extends DatabaseProvider> sourceProvider();

  Class<? extends DatabaseProvider> targetProvider();

}
