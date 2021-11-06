import React from 'react';
import './UrlCard.css';

function UrlCard({name, path, status})  {

    function deleteCard() {
        alert("clicked" + path);
    }

    return (
        <div className="column" title={"My name is " + name + " and my path is " + path}>
            <div className="card">
                <div className="card-header">
                    <div className="url-name">{name}</div>
                    <div className="card-delete" title="Delete me!" onClick={deleteCard}>X</div>
                </div>
                <div className="url-status">{status}</div>
            </div>
        </div>
    );
}

export default UrlCard;