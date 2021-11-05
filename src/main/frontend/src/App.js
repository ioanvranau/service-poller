import React, { useState, useEffect } from 'react';
import './App.css';
import {getAllServiceUrls} from './Api';

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
        <tr key={url.path}>
            <td>{url.name}</td>
            <td>{url.path}</td>
        </tr>
    );

    return (
        <div>
            {/*<div className="search-input">*/}
            {/*    <input onChange={fetchAllUrls} type="text" placeholder="Search"/>*/}
            {/*</div>*/}
            <h1 className="h1">Search Results</h1>
            <div className="books">
                <table>
                    <thead>
                    <tr>
                        <th className="title-col">Name</th>
                        <th className="author-col">Path</th>
                    </tr>
                    </thead>
                    <tbody>{resultUrls}</tbody>
                </table>
            </div>
        </div>

    );
}

export default App;