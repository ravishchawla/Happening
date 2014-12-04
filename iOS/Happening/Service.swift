//
//  Service.swift
//  Happening
//
//  Created by Ravish Chawla on 10/18/14.
//  Copyright (c) 2014 Happening. All rights reserved.
//

import Foundation

class Service {
    
    let ec2URL : String = "http://ec2-54-172-243-211.compute-1.amazonaws.com:3000/";
    var AUTH_URL : String;
    var ADD_USER_URL : String;
    var sessionID : String = ""
    
    init() {
        
        self.AUTH_URL = self.ec2URL + "users/auth";
        self.ADD_USER_URL = self.ec2URL + "users/adduser";
    }
    
/*
    otained at: http://stackoverflow.com/questions/24566180/how-to-post-a-json-with-new-apple-swift-language
    
    func dummy() {
        // create the request & response
        var request = NSMutableURLRequest(URL: NSURL(string: "http://requestb.in/1ema2pl1"), cachePolicy: NSURLRequestCachePolicy.ReloadIgnoringLocalCacheData, timeoutInterval: 5)
        var response: NSURLResponse?
        var error: NSError?
        
        // create some JSON data and configure the request
        let jsonString = "json=[{\"str\":\"Hello\",\"num\":1},{\"str\":\"Goodbye\",\"num\":99}]"
        request.HTTPBody = jsonString.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: true)
        request.HTTPMethod = "POST"
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        
        // send the request
        NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: &error)
        
        // look at the response
        if let httpResponse = response as? NSHTTPURLResponse {
            println("HTTP response: \(httpResponse.statusCode)")
        } else {
            println("No HTTP response")
        }
    }
*/
    
    func loginUser(userName: String, passWord: String) -> Bool {
        
        
        var params = ["username":userName, "userPassword":passWord] as Dictionary<String, String>;
        
        var data: NSDictionary?;
        var status: Int;
        let response = sendPostData(self.AUTH_URL, jsonData: params);
        
        data = response.data;
        status = response.status;
        
        let responseData = data;
        if status == 200 {
            if let value = (responseData!).valueForKey("token") as? String {
                print(value);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if let certainResponse = responseData {
                if let value = certainResponse.valueForKey("msg") as? String {
                    print("\(status):  \(value)");
                    return false;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
    }
    
    
    func signUpUser(name: String, email: String, userName: String, passWord: String, confirmPassword: String) -> Bool {
        
        if(passWord != confirmPassword) {
            return false;
        }
        
        var params = ["username":userName, "userEmail":email, "userPassword":passWord] as Dictionary<String, String>;
        
        //var data: NSDictionary?; //don't need this for sign up
        var status: Int;
        let response = sendPostData(self.ADD_USER_URL, jsonData: params);
        //data = response.data;
        status = response.status;
        
        if(status == -1) {
            print("An error happend");
            return false;
        }
        else {
            print("\(status): Ok, completed");
            return true;
        }
        
    
        
    }
    
    func sendPostData(url: String, jsonData: Dictionary<String,String>) -> (status: Int, data: NSDictionary?) {
        
        var url = NSURL(string: url);
        var request = NSMutableURLRequest(URL: url, cachePolicy: NSURLRequestCachePolicy.ReloadIgnoringLocalCacheData, timeoutInterval: 5);
        
        var response: NSURLResponse?;
        var error: NSError?;
        var status: Int;
        
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(jsonData, options: nil, error: &error);
      //  request.HTTPBody = jsonData.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: true);
        request.HTTPMethod = "POST";
        request.setValue("application/json", forHTTPHeaderField: "Content-Type");
        
        //var json : AnyObject! = NSJSONSerialization.JSONObjectWithData(self.responseData, options: NSJSONReadingOptions.MutableLeaves, error: &error)
            
        var responseData : NSData? =  NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: &error);
    
    
        if error != nil {
            println("Error: ' \(error)");
            return (-1, nil);
        }
        else {
    
        if let nsResponse = response as? NSHTTPURLResponse {
            status = nsResponse.statusCode;
        }
        else {
            status = -1;
        }
    
        if let responseJson = responseData {
            var responseDict: NSDictionary = NSJSONSerialization.JSONObjectWithData(responseJson, options: NSJSONReadingOptions.MutableContainers, error: nil) as NSDictionary;
            
                return (status, responseDict);
        }
            else {
                return (status, nil);
            }
        
        
        }
        //var responseDict: NSDictionary = NSJSONSerialization.JSONObjectWithData(responseData,options: NSJSONReadingOptions.MutableContainers, error:nil) as NSDictionary
        
        
      //  println("response: :::  \(response)");
        
    }
    
    
    
}
