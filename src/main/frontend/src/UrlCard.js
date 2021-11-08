import React from 'react';
import './UrlCard.css';
import {deleteServiceUrl} from "./Api";

function UrlCard({name, path, status, urlEditCallback, refreshUrls, creationTime}) {

    function deleteCard() {
        deleteServiceUrl(path).then(() => {
            refreshUrls();
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
                <div className="url-creation-time">Created:{creationTime}</div>
            </div>
        </div>
    );
}

export default UrlCard;