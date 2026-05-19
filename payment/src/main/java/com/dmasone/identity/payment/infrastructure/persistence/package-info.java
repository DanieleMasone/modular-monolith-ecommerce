/**
 * Payment persistence implementation.
 *
 * <p>JPA entities and Spring Data repositories are private to the payment
 * module. Other modules observe payment behavior through events and application
 * services rather than direct table or repository access.</p>
 */
package com.dmasone.identity.payment.infrastructure.persistence;
