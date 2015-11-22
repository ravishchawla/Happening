//
//  LoginViewController.swift
//  Happening
//
//  Created by Ravish Chawla on 10/18/14.
//  Copyright (c) 2014 Happening. All rights reserved.
//

import UIKit

class SignInViewController: ViewController {
    
    @IBOutlet var nameTextField: UITextField!
    @IBOutlet var emailTextField: UITextField!
    @IBOutlet var userNameTextField: UITextField!
    @IBOutlet var passWordTextField: UITextField!
    @IBOutlet var confirmPassWordTextField: UITextField!
    
    var service: Service = Service();
    
    override func viewDidLoad() {
        super.viewDidLoad();
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning();
    }
    
    @IBAction func signUpAction(sender: AnyObject) {
        
        var name: String = self.nameTextField.text;
        var email: String = self.emailTextField.text;
        var userName: String = self.userNameTextField.text;
        var passWord: String = self.passWordTextField.text;
        var confirmPassWord: String = self.confirmPassWordTextField.text;
        
        self.service.signUpUser(name, email: email, userName: userName, passWord: passWord, confirmPassword: confirmPassWord);
    }
}