package com.bobocode.bibernate.configuration;

import com.bobocode.bibernate.exception.BibernateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DialectResolver {


    private DialectResolver() {
    }

    /**
     * Returns item of {@link SqlDialect} by provided dialect name ignoring case
     * @return value of {@link SqlDialect}
     * @throws BibernateException in case of providing unsupported driver name
     */
    public static SqlDialect resolveDialect(String dialectName) {
        try {
            return SqlDialect.valueOf(dialectName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BibernateException(String.format("Provided SQL dialect < %s > is not supported", dialectName));
        }

    }
}