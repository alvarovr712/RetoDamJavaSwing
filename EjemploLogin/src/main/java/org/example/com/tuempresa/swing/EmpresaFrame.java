package org.example.com.tuempresa.swing;

import org.example.com.tuempresa.swing.model.Solicitud;
import org.example.com.tuempresa.swing.model.Vacante;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class EmpresaFrame extends JFrame{
    private ApiClient apiClient;
    private JPanel contentPanel;

    public EmpresaFrame(){
        setTitle("Empresa");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //Crear un panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        //Crear un menu dentro del panel principal con el boxlayout.Y_AXIS le damos una direccion vertical
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel,BoxLayout.Y_AXIS));


        //Crear los botones de nuestro menú lateral
        JButton botonPublicarVacante = new JButton("Publicar Vacante");

        JButton botonEditarVacante = new JButton("Editar Vacante");

        JButton botonVerTodasVacantes = new JButton("Ver todas las vacantes");

        JButton botonEliminarVacante = new JButton("Eliminar vacante");

        JButton botonAsignarVacante = new JButton("Asignar Vacante");

        JButton botonVerSolicitudes = new JButton("Ver solicitudes");
   

        //Agregamos nuestros botones al menú

        menuPanel.add(botonPublicarVacante);
        menuPanel.add(botonEditarVacante);
        menuPanel.add(botonVerTodasVacantes);
        menuPanel.add(botonEliminarVacante);
        menuPanel.add(botonAsignarVacante);
        menuPanel.add(botonVerSolicitudes);

        //Darle un estilo a los botones
        for(Component item: menuPanel.getComponents()){
            if(item instanceof JButton){
                JButton button = (JButton) item;
                button.setFont(new Font("Arial",Font.PLAIN,16));
                //Centrar los botones
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setMaximumSize(new Dimension(200,400));
            }
        }

        //Contenido principal del panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Bienvenido al sistema !",SwingConstants.CENTER);
        contentPanel.add(label, BorderLayout.CENTER);


        //Con JSplitPane lo que hacemos es dar una separación en este caso entre menuPanel y contentPanel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,menuPanel,contentPanel);
        splitPane.setDividerLocation(250);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);

        setVisible(true);
        apiClient = new ApiClient();

    }
}
