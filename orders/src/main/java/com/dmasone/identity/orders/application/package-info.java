/**
 * Order application services and use-case models.
 *
 * <p>Use cases coordinate module boundaries: they reserve stock through the
 * catalog application API, persist orders through the order repository port,
 * and publish internal events. Idempotency for order placement lives here
 * because it protects the use case from unsafe HTTP retries.</p>
 */
package com.dmasone.identity.orders.application;
