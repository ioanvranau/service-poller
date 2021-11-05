import React from 'react';

export function getAllServiceUrls() {
    return fetch("/api/url")
        .then(response => response.json());
}