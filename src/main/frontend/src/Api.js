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

export function deleteServiceUrl(path) {
    return fetch('api/url/' + path, {method: 'DELETE'});
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