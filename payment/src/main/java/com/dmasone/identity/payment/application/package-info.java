/**
 * Payment use cases and event handling.
 *
 * <p>The application layer reacts to order placement events, invokes the
 * simulated gateway, and persists payment attempts through the payment
 * repository port. It deliberately keeps payment idempotent per order.</p>
 */
package com.dmasone.identity.payment.application;
