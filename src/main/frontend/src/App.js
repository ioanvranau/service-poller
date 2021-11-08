import React, {useEffect, useState} from 'react';
import './App.css';
import {
    addNewServiceUrl,
    getAllServiceUrls,
    ShowErrorMessage,
    ShowSuccessMessage,
    updateNewServiceUrl,
    useInterval
} from './Api';
import UrlCard from './UrlCard';
import "react-notifications/lib/notifications.css";
import {NotificationContainer} from "react-notifications";
import {SliderComponent} from 'react-input-range-slider'

function App() {

    const initialRefreshValue = 2;
    const [isRunning, setIsRunning] = useState(false);
    const [count, setCount] = useState(0);
    const [refreshRate, setRefreshRate] = useState(initialRefreshValue * 1000);
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

    useInterval(() => {
        fetchAllUrls();
        setCount(count + 1);
    }, isRunning ? refreshRate : null);

    const urlNameAndPathToEditChange = (nameAndPath) => {
        setUrlNameToEdit(nameAndPath.name);
        setUrlPathToEdit(nameAndPath.path);
    };
    const resultUrls = (urls || []).map((url) =>
        <UrlCard name={url.name} initialStatus={url.status} path={url.path} key={url.path} creationTime={url.creationTime}
                 urlEditCallback={urlNameAndPathToEditChange} refreshUrls={fetchAllUrls} isRunning={isRunning}/>
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
            ShowSuccessMessage('Service updated successfully');
        }).catch(function (error) {
            if (error.status === 409) {
                ShowErrorMessage('Service already added with path:' + urlPathToEdit);
            } else if (error.status === 406) {
                ShowErrorMessage('Invalid name: ' + urlPathToEdit + ' or path:' + urlPathToEdit);
            } else {
                ShowErrorMessage('Cannot add new service:' + error.statusText);
            }
        });
    }

    function addNewService() {
        addNewServiceUrl(urlNameToEdit, urlPathToEdit).then(() => {
            fetchAllUrls();
            ShowSuccessMessage('Service added successfully');
        }).catch(function (error) {
            if (error.status === 409) {
                ShowErrorMessage('Service already added with path:' + urlPathToEdit);
            } else if (error.status === 406) {
                ShowErrorMessage('Invalid name: ' + urlPathToEdit + ' or path:' + urlPathToEdit);
            } else {
                ShowErrorMessage('Cannot add new service:' + error.statusText);
            }
        });
    }

    function handleRefreshRateChange(e) {
        setRefreshRate(e * 1000);
    }

    function handleIsRunningChange(e) {
        setIsRunning(e.target.checked);
    }

    return (
        <div className="content">
            <NotificationContainer/>
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
                <h3>Click on any URL card name to get url details above and update them if needed.</h3>
                <div className="refresh-container">
                    <input type="checkbox" checked={isRunning} onChange={handleIsRunningChange}/> Running poller
                    <div>Count: {count}</div>
                    <div>Refresh rate</div>
                    <SliderComponent
                        min={0.02}
                        max={initialRefreshValue}
                        step={0.01}
                        value={refreshRate}
                        callback={(value) => handleRefreshRateChange(value)}
                    />
                </div>
                <div className="row">
                    {resultUrls}
                </div>
            </div>
        </div>

    );
}

export default App;