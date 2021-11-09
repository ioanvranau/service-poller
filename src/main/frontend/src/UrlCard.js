import React, {useEffect, useState} from 'react';
import './UrlCard.css';
import {deleteServiceUrl, getServiceUrl, ShowSuccessMessage, useInterval} from "./Api";

function UrlCard({
                     name,
                     path,
                     initialStatus,
                     urlEditCallback,
                     refreshUrls,
                     creationTime,
                     initialIsRunning,
                     refreshRate
                 }) {

    const [status, setStatus] = useState(initialStatus);
    const [isRunning, setIsRunning] = useState(initialIsRunning);
    useEffect(() => {
        setIsRunning(initialIsRunning)
    }, [initialIsRunning])

    function deleteCard() {
        deleteServiceUrl(path).then(() => {
            refreshUrls();
            ShowSuccessMessage('Service deleted successfully');
        });
    }

    useInterval(() => {
        getServiceUrl(path).then((response) => {
            setStatus(response)
            ShowSuccessMessage('Service polled successfully');
        });
    }, isRunning ? refreshRate : null);

    function handleIsRunningChange(e) {
        setIsRunning(e.target.checked);
    }

    function getCardData() {
        urlEditCallback({name: name, path: path});
    }

    return (
        <div className="column" title={"My name is " + name + " and my path is " + path}>
            <div className="card">
                <div className="card-header">
                    <div className="url-name" onClick={getCardData}>{name}</div>
                    <div className="card-delete" title="Delete me!" onClick={deleteCard}>X</div>
                </div>
                <div className="url-path">{window.location.href}{path}</div>
                <div className="url-status">{status}</div>
                <input type="checkbox" checked={isRunning} onChange={handleIsRunningChange}/> Running
                <div className="url-creation-time">Created:{creationTime}</div>
            </div>
        </div>
    );
}

export default UrlCard;