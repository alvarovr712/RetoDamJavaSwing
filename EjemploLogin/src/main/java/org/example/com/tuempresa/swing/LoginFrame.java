package org.example.com.tuempresa.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame{

    private JTextField usernameField;
    private JTextField passwordField;

    public LoginFrame(){
        setTitle("Login");
        setSize(400,200);
        //La aplicacion se parara al cerrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*Centra la ventana en la pantalla, si quieres que se centre en otra ventana por ejemplo JFrame en vez de null pasarias el valor al que
          quieres que se centre.
        * */
        setLocationRelativeTo(null);
        /*Crear el panel con GridBagLayout asi podemos ir colocando en los ejes x e y todos los componentes como haciamos en TKinter con
         el .grid()*/
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints colocar = new GridBagConstraints();
        //Espacio entre los componentes
        colocar.insets = new Insets(10,10,10,10);

        //AÑADIR LABEL USUARIO COLOCADO
        colocar.gridx = 0;
        colocar.gridy = 0;
        colocar.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Usuario"),colocar);

        // Añadir el campo de texto para el usuario
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30)); // Tamaño del campo
        colocar.gridx = 1;
        colocar.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, colocar);

        // AÑADIR LABEL CONTRASEÑA
        colocar.gridx = 0;
        colocar.gridy = 1;
        colocar.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Contraseña:"), colocar);

        // Añadir el campo de texto para la contraseña
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30)); // Tamaño del campo
        colocar.gridx = 1;
        colocar.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, colocar);

        // Crear un botón
        JButton loginButton = new JButton("Iniciar sesión");
            //Añadirle el tipo de letra
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
            //El color de relleno del botón
        loginButton.setBackground(Color.GRAY);
            //El color de la letra del botón
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new LoginAction());

        // Añadir el botón al panel
        colocar.gridx = 0;
        colocar.gridy = 2;
        colocar.gridwidth = 2; // El botón ocupará las dos columnas
        colocar.anchor = GridBagConstraints.CENTER; // Centra el botón
        panel.add(loginButton, colocar);


        //Añadir el panel a la ventana
        add(panel);
        //Hacer la ventana visible
        setVisible(true);

    }
    //Vamos a crear un metodo para saber que tiene que hacer nuestro botón al pulsarlo
    private class LoginAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //Obtener el texto del campo usuario y contraseña
            String username = usernameField.getText();
            String password = new String(passwordField.getText());
            autentificador(username,password);


        }

        // DONDE SE ELEJE QUE VENTA ABRIR USAR respuesta.equals("OK2") 

        private void autentificador(String username, String password){
            //Crear un ApiCliente para hacer solicitudes al login de una API
            ApiClient apiClient = new ApiClient();
            String respuesta = apiClient.enviarRespuesta(username,password);

            if(respuesta.equals("OK1")){
                JOptionPane.showMessageDialog(LoginFrame.this, "Inicio de sesión correcto");

                LoginFrame.this.dispose();

                new EmpresaFrame();
            }else if(respuesta.equals("OK2")){
                JOptionPane.showMessageDialog(LoginFrame.this, "Inicio de sesión correcto");

                LoginFrame.this.dispose();

                new AdministradorFrame();
            }else {
                JOptionPane.showMessageDialog(LoginFrame.this,"Usuario contraseña incorrecto");
            }

        }
    }


}
