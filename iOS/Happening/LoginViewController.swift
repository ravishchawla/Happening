//
//  LoginViewController.swift
//  Happening
//
//  Created by Ravish Chawla on 10/18/14.
//  Copyright (c) 2014 Happening. All rights reserved.
//

import UIKit

class LoginViewController: ViewController {
    @IBOutlet var userNameTextField: UITextField!
    @IBOutlet var passWordTextField: UITextField!
    
    var service: Service = Service();
    
    let nycImage = "Images/NYC.jpg";
    
    override func viewDidLoad() {
        super.viewDidLoad();
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning();
    }
    
    
    @IBAction func signInAction(sender: AnyObject!	) {
        
        var userName: String = userNameTextField.text;
        var passWord: String = passWordTextField.text;
        
        var verified: Bool = self.service.loginUser(userName, passWord: passWord);
        
        print("username: \(userName)");
        print("password: \(passWord)");
        print("\(verified)");
        
    }
    
}