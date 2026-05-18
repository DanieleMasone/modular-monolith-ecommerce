package com.dmasone.identity.ecommerceapp.web;

/**
 * Stable error shape returned by REST endpoints for validation and domain
 * failures.
 */
public record ApiError(String code, String message) {
}
