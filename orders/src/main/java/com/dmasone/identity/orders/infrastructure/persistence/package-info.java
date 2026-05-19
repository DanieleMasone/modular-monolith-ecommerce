/**
 * Order persistence implementation.
 *
 * <p>JPA mappings and Spring Data repositories live behind the order
 * repository port. They store the order aggregate and operational metadata such
 * as idempotency keys without leaking repository types to other modules.</p>
 */
package com.dmasone.identity.orders.infrastructure.persistence;
