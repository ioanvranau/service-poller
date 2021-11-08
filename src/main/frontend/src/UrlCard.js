import React, {useState} from 'react';
import './UrlCard.css';
import {deleteServiceUrl, getServiceUrl, ShowSuccessMessage} from "./Api";

function UrlCard({name, path, initialStatus, urlEditCallback, refreshUrls, creationTime, isRunning}) {

    const [status, setStatus] = useState(initialStatus);

    function deleteCard() {
        deleteServiceUrl(path).then(() => {
            refreshUrls();
            ShowSuccessMessage('Service deleted successfully');
        });
    }

    function updateServiceUrlStatus() {
        getServiceUrl(path).then((response) => {
            let statusText = response.statusText;
            setStatus(statusText)
            ShowSuccessMessage('Service polled successfully');
        });
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
                <button onClick={updateServiceUrlStatus}>Click</button>
                <div className="url-creation-time">Created:{creationTime}</div>
            </div>
        </div>
    );
}

export default UrlCard;