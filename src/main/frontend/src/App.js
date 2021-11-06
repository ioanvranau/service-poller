import React, { useState, useEffect } from 'react';
import './App.css';
import {getAllServiceUrls} from './Api';
import UrlCard from './UrlCard';

function App() {

    const [urls, setUrls] = useState(0);

    const fetchAllUrls = () => {
        getAllServiceUrls().then(response => {
            setUrls(response);
        });
    };

    useEffect(() => {
        fetchAllUrls();
    }, []);

    const resultUrls = (urls || []).map((url) =>
        <UrlCard name={url.name} status={url.status} path={url.path}/>
    );

    return (
        <div className="content">
            <div>
                <h2>Add new service</h2>
                <div className="add-container">
                    <div>Service name</div>
                    <input />
                    <div>Service path</div>
                    <input />
                    <button onClick={fetchAllUrls}>Add</button>
                </div>
            </div>
            <div className="url-cards-container">
                <div className="row">
                {resultUrls}
                </div>
            </div>
        </div>

    );
}

export default App;