import {NotificationManager} from "react-notifications";
import {useEffect, useRef} from 'react';

export function getAllServiceUrls() {
    return fetch("/api/url")
        .then(response => response.json());
}

export function updateNewServiceUrl(name, path) {
    const requestOptions = {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, path: path})
    };
    return fetch('/api/url', requestOptions)
        .then(handleErrors)
        .then(response => response);
}

export function addNewServiceUrl(name, path) {
    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, path: path})
    };
    return fetch('/api/url', requestOptions)
        .then(handleErrors)
        .then(response => {
            return response.json();
        })
}

export function saveStatsForServiceUrl(name, path, status) {
    const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, path: path, status: status})
    };
    fetch('/api/urlstats', requestOptions)
        .then(() => {/* ignore it. just stats*/
        });
}

export function deleteServiceUrl(path) {
    const requestOptions = {
        method: 'DELETE',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({path: path})
    };
    return fetch('api/url', requestOptions);
}

export function getServiceUrl(path) {
    return fetch(path).then(response => response.text());
}


export function ShowSuccessMessage(message) {
    NotificationManager.success(message, "", 2000);
}

export function ShowErrorMessage(message) {
    NotificationManager.error(message, "", 2000);
}

function handleErrors(response) {
    if (!response.ok) {
        throw response;
    }
    return response;
}


export const useInterval = (callback, delay) => {

    const savedCallback = useRef();

    useEffect(() => {
        savedCallback.current = callback;
    }, [callback]);


    useEffect(() => {
        function tick() {
            savedCallback.current();
        }

        if (delay !== null) {
            const id = setInterval(tick, delay);
            return () => clearInterval(id);
        }
    }, [delay]);
}