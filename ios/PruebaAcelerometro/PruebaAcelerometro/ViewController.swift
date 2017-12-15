//
//  ViewController.swift
//  PruebaAcelerometro
//
//  Created by Ion Jaureguialzo Sarasola on 14/12/17.
//  Copyright © 2017 Ion Jaureguialzo Sarasola. All rights reserved.
//

import UIKit

import CoreMotion

import Alamofire

class ViewController: UIViewController {

    @IBOutlet weak var labelX: UILabel!
    @IBOutlet weak var labelY: UILabel!
    @IBOutlet weak var labelZ: UILabel!

    @IBOutlet weak var sliderH: UISlider!
    @IBOutlet weak var sliderV: UISlider!

    @IBOutlet weak var enviar: UISwitch!
    @IBOutlet weak var servidor: UILabel!
    
    let IP = "192.168.1.103"

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.

        actualizarPantalla()

        servidor.text = " "
        
        // REF: Girar el slider: https://stackoverflow.com/a/25522456/5136913
        self.sliderV.transform = CGAffineTransform(rotationAngle: .pi / 2)

    }

    @IBAction func botonIniciar(_ sender: UIButton) {
        startDeviceMotion()
    }

    @IBAction func botonParar(_ sender: UIButton) {
        actualizarPantalla()

        sliderH.value = 0
        sliderV.value = 0

        timer?.invalidate()
        timer = nil
        n = 0
    }
    
    @IBAction func switchEnviar(_ sender: UISwitch) {
        servidor.text = sender.isOn ? "IP: \(IP)" : " "
    }
    
    func actualizarPantalla(_ x: Double? = nil, _ y: Double? = nil, _ z: Double? = nil) {

        if let x = x {
            self.labelX.text = String(format: "x: %.3f", x)
        } else {
            self.labelX.text = "x"
        }

        if let y = y {
            self.labelY.text = String(format: "y: %.3f", y)
        } else {
            self.labelY.text = "y"
        }

        if let z = z {
            self.labelZ.text = String(format: "z: %.3f", z)
        } else {
            self.labelZ.text = "z"
        }

    }

    let motion = CMMotionManager()

    var timer: Timer?

    var n = 0

    // REF: https://developer.apple.com/documentation/coremotion/getting_raw_accelerometer_events
    // REF: https://developer.apple.com/documentation/coremotion/getting_processed_device_motion_data
    func startDeviceMotion() {
        if motion.isDeviceMotionAvailable {
            self.motion.deviceMotionUpdateInterval = 1.0 / 60.0
            self.motion.showsDeviceMovementDisplay = true
            self.motion.startDeviceMotionUpdates(using: .xArbitraryCorrectedZVertical)

            // Evitamos crear más de un Timer
            if self.timer == nil {

                // Configure a timer to fetch the motion data.
                self.timer = Timer(fire: Date(), interval: (1.0 / 60.0), repeats: true,
                                   block: { (timer) in
                                       if let data = self.motion.deviceMotion {
                                           // Get the attitude relative to the magnetic north reference frame.
                                           let x = data.attitude.pitch
                                           let y = data.attitude.roll
                                           let z = data.attitude.yaw

                                           // Use the motion data in your app.
                                           // REF: https://stackoverflow.com/a/27339287/5136913
                                           self.actualizarPantalla(x, y, z)

                                           self.sliderH.value = Float(y)
                                           self.sliderV.value = Float(x)

                                           if self.enviar.isOn {
                                               // REF: Servidor: https://stackoverflow.com/a/44013423/5136913

                                               // REF: Alamofire: https://github.com/Alamofire/Alamofire/blob/master/Documentation/Usage.md#http-methods
                                               let parameters: Parameters = [
                                                   "n": self.n,
                                                   "x": x,
                                                   "y": y,
                                                   "z": z,
                                               ]
                                               Alamofire.request("http://\(self.IP):3000/", parameters: parameters)
                                               self.n += 1
                                           }

                                       }
                                   })

                // Add the timer to the current run loop.
                RunLoop.current.add(self.timer!, forMode: .defaultRunLoopMode)

            }
        }
    }

}

