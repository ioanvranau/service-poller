export function getAllServiceUrls() {
    return fetch("/api/url")
        .then(response => response.json());
}

export function addNewServiceUrl(name, path) {
    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, path: path})
    };
    return fetch('/api/url', requestOptions)
        .then(response => response.json());
}