/**
 * Shared domain abstractions used across modules.
 *
 * <p>This package is intentionally small: it contains stable concepts such as
 * domain events, the event publisher port, and the base domain exception type.
 * It must not contain application use cases, persistence concerns, web DTOs,
 * or dependencies on business modules.</p>
 */
package com.dmasone.identity.sharedkernel.domain;
