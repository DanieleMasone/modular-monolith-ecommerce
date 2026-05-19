/**
 * Catalog persistence implementation.
 *
 * <p>JPA entities and Spring Data repositories are kept module-local. They
 * implement catalog storage without becoming a public integration surface for
 * orders, payment, or the application bootstrap.</p>
 */
package com.dmasone.identity.catalog.infrastructure.persistence;
