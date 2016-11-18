# REST API 


##  Table of contents
<!-- TOC depthFrom:1 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [1 General Overview ](#1-general-overview)
- [2 Common Error Results ](#2-common-error-results)
   - [2.1 Generic Error Response ](#21-generic-error-response)
   - [2.2 Error Response for not implemented methods ](#22-error-response-for-not-implemented-methods)

- [3 QA Service Endpoints ](#3-qa-service-endpoints)
   - [3.1 Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)
      - [3.1.1 Synchronous QA for a single file with schema validation](#311-synchronous-qa-for-a-single-file-with-schema-validation)
   
   - [3.2 Asynchronous QA for a single file](#32-asynchronous-qa-for-a-single-file)
   - [3.3 Asynchronous QA for an Envelope](#33-asynchronous-qa-for-an-envelope)
   - [3.4 Get QA result of a Job Status](#34-get-qa-result-of-a-job-status)
   - [3.5 Get list of QA Scripts for  a Schema ](#35-get-list-of-qa-scripts-for-a-schema)
- [4 Security ](#4-security)
  - [4.1 Required Claims of the JWT Token](#41-required-claims-of-the-jwt-token)
  - [4.2 Token Creation](#42-token-creation)
  - [4.3 Token Transmission - Validation Flow](#43-token-transmission---validation-flow)
  - [4.4 Example of a Secured API Endpoint for the Asynchronous QA of an Envelope](#44-example-of-a-secured-api-endpoint-for-the-asynchronous-qa-of-an-envelope)


  
</br>
</br>
</br>

## 1 General Overview
This documentation is the result of the ongoing conversation in this ticket:
https://taskman.eionet.europa.eu/issues/29005 regarding the REST API of the xmlconv application.


## 2 Common Error Results


### 2.1 Generic Error Response


  ```json
    {
     "httpStatusCode": 1234,
     "errorMessage"  : "error message" 
    }
   ```
### 2.2 Error Response for not implemented methods


  ```json
    {
     "errorMessage": "asynchronous QA for a Single file is not supported yet",
     "httpStatusCode": "501"
    }
   ```
    
## 3 QA Service Endpoints 


### 3.1 Synchronous QA for a single file


  Wrapper around XQueryService.runQAScript

* **URL**

  /restapi/qajobs

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
        "sourceUrl":"http://www.example.com",
        "scriptId":"42" 
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
     {
        "feedbackStatus": "ERROR",
        "feedbackMessage": "Some message",
        "feedbackContentType": "text/html;charset=UTF-8",
        "feedbackContent": "<div>...</div>" 
     }
    ```
 
* **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed sourceUrl <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter sourceUrl cannot be null" 
    }
    ```

* **Error Response:**
  
   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed scriptId <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter scriptId cannot be null" 
    }
    ```
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** qA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```

--
### 3.1.1 Synchronous QA for a single file with schema validation
  
  The endpoint is the same as [Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)
   except that we pass the parameter `scriptId` with value `-1` in order to enable schema validation.
   
* **Example of Data Params(Http Request Body)**


  ```json
    {
     "sourceUrl": "www.example.com",
     "scriptId"  : "-1" 
    }
   ```
* **Success Response:**
  
  See: [Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)

* **Error Response:**
  
  See: [Synchronous QA for a single file](#31-synchronous-qa-for-a-single-file)

--
### 3.2 Asynchronous QA for a single file


 
* **URL**

  /restapi/asynctasks/qajobs

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
        "sourceUrl":"http://www.example.com",
        "scriptId":"42" 
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
     {
        "jobId":1234
     }
    ```
 
* **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed sourceUrl <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter sourceUrl cannot be null" 
    }
    ```

* **Error Response:**
  
   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed scriptId <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter scriptId cannot be null" 
    }
    ```
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```
 
-- 
### 3.3 Asynchronous QA for an Envelope
 

* **URL**

  /restapi/asynctasks/qajobs/batch

* **Method:**

  `POST`
  
* **Content-Type:** application/json

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
    "envelopeUrl":"http://cdrtest.eionet.europa.eu/gr/envelope1234"
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
    "jobs": [
        {
            "id": 123,
            "fileUrl": "http://some.file.url.1" 
        }, {
            "id": 456,
            "fileUrl": "http://some.file.url.2" 
        }
    ]
    ```
 
* **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** missing or malformed envelopeUrl <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage"  : "Parameter envelopeUrl cannot be null" 
    }
    ```


    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```



-- 
### 3.4 Get QA result of a Job Status
 

* **URL**

  /restapi/asynctasks/qajobs/[jobid]

* **Method:**

  `GET`
  
* **Content-Type:** none

*  **URL Params**

   - **parameter:** id <br>
     **required:** yes <br>
     **allowedValues:** integers <br>
     **defaultValue:** none
  
* **Data Params**
  
   none
    

* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
    {
     "executionStatus": "completed",
     "feedbackStatus": "ERROR",
     "feedbackMessage": "Some message",
     "feedbackContentType": "text/html",
     "feedbackContent": "<div>...</div>" 
    }
    ```
 
    
* **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```


-- 
### 3.5 Get list of QA Scripts for a schema

* **URL**

  /restapi/qascripts

* **Method:**

  `GET`
  
* **Content-Type:** none

*  **URL Params**


   - **parameter:** schema <br>
     **required:** no <br>
     **allowedValues:** any String <br>
     **defaultValue:** none

   - **parameter:** active <br>
     **required:** no <br>
     **allowedValues:** true ,false , all <br>
     **defaultValue:** true

  

* **Data Params**
  
   none
    

* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
  
   ```json
  [
    {
     "schemaId": "26",
     "queryId": "-1",
     "upperLimit": "200",
     "xmlSchema": "eper.xsd",
     "type": "xsd",
     "query": "eper.xsd",
     "shortName": "XML Schema Validation",
     "contentType": "text/html;charset=UTF-8",
     "description": "EPER",
     "contentTypeId": "HTML"
   },
   {
     "schemaId": "420",
     "queryId": "-1",
     "upperLimit": "200",
     "xmlSchema": "http://acm.eionet.europa.eu/schemas/reg2009443ec/cars_aggregated-2012.xsd",
     "type": "",
     "query": "http://acm.eionet.europa.eu/schemas/reg2009443ec/cars_aggregated-2012.xsd",
     "shortName": "XML Schema Validation",
     "contentType": "text/html;charset=UTF-8",
     "description": "",
     "contentTypeId": "HTML"
    }
  ]
    ```
  
 * **Error Response:**

   **Code:** 400 Bad Request <br />
   **Reason:** wrong value of the active parameter send  <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 400,
     "errorMessage": "parameter active value must be one of :[true, false, all]"
    }
    ```
  
  
  
  
 * **Error Response:**
      
   **Code:** 500 Internal Server Error <br />
   **Reason:** QA Service Exception <br/>
    **Content:** 
    ```json
    {
     "httpStatusCode": 500,
     "errorMessage"  : "QA Service Exception" 
    }
    ```

  
  
    
-- 

 

## 4 Security
 We have implemented a way to secure the end points utilizing JWT tokens and Spring Security Framework.
 Below you can find implementation details  as well as a secured endpoint which can be used for testing the mechanism.<br>
 **Important Note:** This is a proof of concept  to get us started regarding how to secure the endpoints.
 
 
### 4.1 Required Claims of the JWT token
####  Standard JWT Claims 
   **iss:**     The issuer of the token.<br>
   **subject:** Subject- Identifier (or, name) of the user this token represents. The value of this field must be an        
                existing user in the **XMLCONV Database Table** storing the authorized users and their state(enabled/disabled).<br>
   **exp:**     The expiration date of the token. Should be later than current date.<br>
   **aud:**     The audience - Intended recipient of this token

####  Additional JWT Claims 
 none at the moment 

#### JWT Key
The Key used to sign the JWT token

### 4.2 Token Creation
Ideally we should expose an endpoint which whould accept a number of parameters and create a token as a response, but we left this functionality out of the current implementation until we agree we actually need it.

### 4.3 Token Transmission - Validation flow
####  Client Side:
  Each HTTP Request on a secured API endpoint should contain an **HTTP Header** with a **key-value** pair , as shown below:
  
* **X-Auth-Token:** generated-token-goes-here

####  Server Side:
 Spring Security is configured to filter incoming URLS and perform security filtering on those under **/auth**<br>
 The back-end mechanism will inspect the Http-Request looking for the HTTP Header:  **X-Auth-Token**.<br>
 The validation mechanism then decodes the token and checks the following:<br>
 - If the claims: **iss , aud**  exist and also that they  match the values explicitly set in the application.<br>
 - If the claim : **exp** exists and that it is not before the current date, meaning that the token has expired.<br>
 - If the **Key** used to sign the Token, matches the explicitly set key in the application.<br>
 - If the claim: **sub** exists, and also search the Database table **T_API_USER** for an enabled **user** with this value as<br>   **username**.<br>
 When all the above checks are successfull, The mechanism will clarify the request as authenticated and allow the application to continue its normal workflow and serve the request.
 The session is not stored between requests from the same source, so each time a request is made against the secured endpoint,the request must contain a valid token.
 

### 4.4 Example of a Secured API Endpoint for the Asynchronous QA of an Envelope
 
 Visit: http://jwtbuilder.jamiekurtz.com/ to obtain a JWT token <br>
 Submit the following values: <br>
 **issuer:** eea<br>
 **aud:** eea<br>
 **sub:** admin<br>
 
 **JWT KEY:** top-secret<br>
 Use HS256 as a signing algorithm. <br>
 
 
* **URL**

  /restapi/auth/asynctasks/qajobs/batch

* **Method:**

  `POST`
 
* **HTTP-Headers**

  * **Content-Type:** application/json
  * **X-Auth-Token:** place-generated-token-here

*  **URL Params**

   none

* **Data Params**
  ```json  
    {
    "envelopeUrl":"http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556"
    }
  ```
    


* **Success Response:**

   **Code:** 200 OK <br />
   **Content:** 
  
   ```json
    "jobs": [
        {
            "id": 89,
            "fileUrl": "http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AggDataByWaterBody_SampleTestFile_20151111_AggregatedDataByWaterBody.xml" 
       }  
         ]
    ```
* **Error Response:**

   **Code:** 401 Unauthorized <br />
   **Reason:** missing or invalid JWT Token <br/>
