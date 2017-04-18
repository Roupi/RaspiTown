# RaspiTown WebApp

# Mode deployement(.war)

0.Autoriser l'app à accéder au port usb du phidget
        lsusb pour voir sur quel port est connecté le phidget (ici 001)
        sudo chmod o+w /dev/bus/usb/001/ pour autoriser le port 001        
1.Configurer tomcat pour qu'il pointe vers JAVA 8
         /etc/tomcat7/tomcat7.conf modifier le JAVA_HOME
