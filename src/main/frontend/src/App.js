import React, {useEffect, useState} from 'react';
import './App.css';
import './utils/buttons.css';
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
    const [isRunningCounter, setIsRunningCounter] = useState(true);
    const [isRunningAllServicePoller, setIsRunningAllServicePoller] = useState(false);
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
        setCount(count + 1);
    }, isRunningCounter ? refreshRate : null);

    const urlNameAndPathToEditChange = (nameAndPath) => {
        setUrlNameToEdit(nameAndPath.name);
        setUrlPathToEdit(nameAndPath.path);
    };
    const resultUrls = (urls || []).map((url) =>
        <UrlCard name={url.name} initialStatus={url.status ? url.status : 'IDLE' } path={url.path} key={url.path}
                 creationTime={url.creationTime}
                 urlEditCallback={urlNameAndPathToEditChange} refreshUrls={fetchAllUrls}
                 initialIsRunning={isRunningAllServicePoller} refreshRate={refreshRate}/>
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
            if (error.status === 404) {
                ShowErrorMessage('Service do not exists. Path:' + urlPathToEdit);
            } else if (error.status === 406) {
                ShowErrorMessage('Invalid name: ' + urlPathToEdit + ' or path:' + urlPathToEdit);
            } else {
                ShowErrorMessage('Cannot update service:' + error.statusText);
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

    function startPollingForAllServices() {
        if (isRunningAllServicePoller) {
            setIsRunningAllServicePoller(false)
        } else {
            setIsRunningAllServicePoller(true)
        }
    }

    function handleIsRunningCounterChange(e) {
        setIsRunningCounter(e.target.checked);
    }

    let startButtonClassName = isRunningAllServicePoller ? "button-red" : "button-gradient";
    let startButtonLabel = isRunningAllServicePoller ? "Stop all" : "Start all";

    return (
        <div className="content">
            <NotificationContainer/>
            <div>
                <h2>Add new service</h2>
                <h5>Service path will be relative to {window.location.href} Valid examples: path, path/1/new, path/3</h5>
                <div className="add-container">
                    <div>Service name</div>
                    <input value={urlNameToEdit} onChange={handleNameChange}/>
                    <div>Relative service path</div>
                    <input value={urlPathToEdit} onChange={handlePathChange}/>
                    <button onClick={addNewService} className="blue-button">Add</button>
                    <button onClick={updateService} className="blue-button update-button">Update by path</button>
                    <button onClick={fetchAllUrls} className="blue-button">Refresh</button>
                </div>
            </div>
            <div className="url-cards-container">
                <h3>Click on any URL card name to get url details above and update the name if needed.</h3>
                <div className="start-all">
                    <h6>Here is just a counter to see that the polling is working</h6>
                    <button title="Start polling for all services" onClick={startPollingForAllServices}
                            className={startButtonClassName}>{startButtonLabel}
                    </button>
                </div>
                <div className="refresh-container">
                    <div><input
                        type="checkbox" checked={isRunningCounter}
                        onChange={handleIsRunningCounterChange}/> Running counter
                    </div>
                    <div>Count: {count}</div>
                    <div>Refresh rate</div>
                    <SliderComponent
                        min={0.1}
                        max={initialRefreshValue}
                        step={0.1}
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