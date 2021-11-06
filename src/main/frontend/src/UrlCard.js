import React from 'react';
import './UrlCard.css';

function UrlCard({name, path, status})  {

    function deleteCard() {
        alert("clicked" + path);
    }

    return (
        <div className="column">
            <div className="card">
                <div className="card-header">
                    <div className="url-name">{name}</div>
                    <div className="card-delete" onClick={deleteCard}>X</div>
                </div>
                <div className="url-status">{status}</div>
            </div>
        </div>
    );
}

export default UrlCard;