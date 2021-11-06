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
        <UrlCard name={url.name} status={"OK"}/>
    );

    return (
        <div>
            <div className="root">
                {resultUrls}
            </div>
        </div>

    );
}

export default App;