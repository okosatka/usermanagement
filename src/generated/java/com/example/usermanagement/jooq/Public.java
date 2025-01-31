/*
 * This file is generated by jOOQ.
 */

package com.example.usermanagement.jooq;


import com.example.usermanagement.jooq.tables.TbUser;
import com.example.usermanagement.jooq.tables.TbUserExternalProject;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * All users
     */
    public final TbUser TB_USER = TbUser.TB_USER;

    /**
     * External Project identifier for users
     */
    public final TbUserExternalProject TB_USER_EXTERNAL_PROJECT = TbUserExternalProject.TB_USER_EXTERNAL_PROJECT;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
                TbUser.TB_USER,
                TbUserExternalProject.TB_USER_EXTERNAL_PROJECT
        );
    }
}
