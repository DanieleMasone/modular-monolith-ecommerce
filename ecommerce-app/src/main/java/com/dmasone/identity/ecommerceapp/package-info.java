/**
 * Application bootstrap for the modular monolith.
 *
 * <p>The bootstrap module assembles catalog, orders, payment, and shared-kernel
 * into one Spring Boot application. It owns runtime wiring but should not
 * contain business rules that belong inside a domain module.</p>
 */
package com.dmasone.identity.ecommerceapp;
