/**
 * Catalog domain model and stock rules.
 *
 * <p>Classes in this package express catalog business invariants without
 * depending on Spring, HTTP, Redis, or JPA. Stock reservation belongs here so
 * other modules cannot bypass catalog ownership by touching persistence
 * directly.</p>
 */
package com.dmasone.identity.catalog.domain;
