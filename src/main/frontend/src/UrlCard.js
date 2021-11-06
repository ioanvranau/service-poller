import React from 'react';
import './UrlCard.css';

const UrlCard = props => {

    return (
        <div className="card">
            <div className="url-name">{props.name}</div>
            <div className="url-status">{props.status}</div>
        </div>
    );
}

export default UrlCard;