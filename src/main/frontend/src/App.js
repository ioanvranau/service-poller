import React, {useEffect, useState} from 'react';
import './App.css';
import {addNewServiceUrl, getAllServiceUrls, updateNewServiceUrl} from './Api';
import UrlCard from './UrlCard';

function App() {

    const [urls, setUrls] = useState(0);
    const [urlNameToEdit, setUrlNameToEdit] = useState('');
    const [urlPathToEdit, setUrlPathToEdit] = useState('');

    const fetchAllUrls = () => {
        getAllServiceUrls().then(response => {
            setUrls(response);
        });
    };

    useEffect(() => {
        fetchAllUrls();
    }, []);

    const urlNameAndPathToEditChange = (nameAndPath) => {
        setUrlNameToEdit(nameAndPath.name);
        setUrlPathToEdit(nameAndPath.path);
    };
    const resultUrls = (urls || []).map((url) =>
        <UrlCard name={url.name} status={url.status} path={url.path} key={url.path}
                 urlEditCallback={urlNameAndPathToEditChange} refreshUrls={fetchAllUrls}/>
    );

    function handleNameChange(event) {
        setUrlNameToEdit(event.target.value);
    }

    function handlePathChange(event) {
        setUrlPathToEdit(event.target.value);
    }

    function updateService() {
        updateNewServiceUrl(urlNameToEdit, urlPathToEdit).then(() => {
                fetchAllUrls();
        });
    }

    function addNewService() {
        addNewServiceUrl(urlNameToEdit, urlPathToEdit).then(response => {
            if (response.error) {
                alert(response.error);
            } else {
                fetchAllUrls();
            }
        });
    }

    return (
        <div className="content">
            <div>
                <h2>Add new service</h2>
                <div className="add-container">
                    <div>Service name</div>
                    <input value={urlNameToEdit} onChange={handleNameChange}/>
                    <div>Service path</div>
                    <input value={urlPathToEdit} onChange={handlePathChange}/>
                    <button onClick={addNewService} className="blue-button">Add</button>
                    <button onClick={updateService} className="blue-button">Update</button>
                    <button onClick={fetchAllUrls} className="blue-button">Refresh</button>
                </div>
            </div>
            <div className="url-cards-container">
                <h3>Click on any URL card name to get data for update</h3>
                <div className="row">
                    {resultUrls}
                </div>
            </div>
        </div>

    );
}

export default App;