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

public class DashboardFrame extends JFrame {
    private ApiClient apiClient;
    private JPanel contentPanel;
    public DashboardFrame() {
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
        botonPublicarVacante.addActionListener(e -> publicarVacante());

        JButton botonEditarVacante = new JButton("Editar Vacante");
        botonEditarVacante.addActionListener(e -> modificarVacante());

        JButton botonVerTodasVacantes = new JButton("Ver todas las vacantes");
        botonVerTodasVacantes.addActionListener(e ->mostrarVacantes());

        JButton botonEliminarVacante = new JButton("Eliminar vacante");
        botonEliminarVacante.addActionListener(e -> cancelarVacante());

        JButton botonAsignarVacante = new JButton("Asignar Vacante");
        botonAsignarVacante.addActionListener(e -> asignarVacante());

        JButton botonVerSolicitudes = new JButton("Ver solicitudes");
        botonVerSolicitudes.addActionListener(e -> mostrarFormulario());

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

    private void mostrarVacantes(){

        // Limpiamos el contenido de nuestro panel
        contentPanel.removeAll();

        // Hacemos la llamada a la API para obtener los datos

        List<Vacante> vacantes = apiClient.obtenerVacantes();

        // Columnas para la tabla
        String[] columnNames = {"ID Vacante", "Nombre", "Descripción", "Fecha", "Salario", "Imagen", "Detalles", "Estatus"};
        Object[][] data = new Object[vacantes.size()][8];

        // Insertamos los datos que hemos obtenido de la API
        for (int i = 0; i < vacantes.size(); i++) {
            Vacante vacante = vacantes.get(i);
            data[i][0] = vacante.getIdVacante();
            data[i][1] = vacante.getNombre();
            data[i][2] = vacante.getDescripcion();
            data[i][3] = vacante.getFecha();
            data[i][4] = vacante.getSalario();
            data[i][5] = vacante.getImagen();
            data[i][6] = vacante.getDetalles();
            data[i][7] = vacante.getEstatus();
        }

        // Crear el modelo de la tabla
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        // Crear la tabla y agregarla al panel de contenido
        JTable vacantesTable = new JTable(tableModel);
        vacantesTable.setFillsViewportHeight(true);
        vacantesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Crear un JScrollPane para la tabla
        JScrollPane scrollPane = new JScrollPane(vacantesTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);


        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void publicarVacante() {

        contentPanel.removeAll();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints colocar = new GridBagConstraints();
        colocar.insets = new Insets(20, 20, 20, 20); // Mayor espacio entre componentes
        colocar.fill = GridBagConstraints.HORIZONTAL;

        JTextField nombreField = new JTextField(25);  // Campo de texto más grande
        JTextField descripcionField = new JTextField(25);  // Campo de texto más grande
        JTextField salarioField = new JTextField(25);  // Campo de texto más grande
        JTextField imagenField = new JTextField(25);  // Campo de texto más grande
        JTextField detallesField = new JTextField(25);  // Campo de texto más grande

        
        Font labelFont = new Font("Arial", Font.BOLD, 18);

        
        colocar.gridx = 0; colocar.gridy = 0;
        JLabel nombreLabel = new JLabel("Nombre:");
        nombreLabel.setFont(labelFont);  
        formPanel.add(nombreLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(nombreField, colocar);

        colocar.gridx = 2; colocar.gridy = 0;
        JLabel descripcionLabel = new JLabel("Descripción:");
        descripcionLabel.setFont(labelFont);  // Cambiar fuente
        formPanel.add(descripcionLabel, colocar);
        colocar.gridx = 3;
        formPanel.add(descripcionField, colocar);


        colocar.gridx = 0; colocar.gridy = 1;
        JLabel salarioLabel = new JLabel("Salario:");
        salarioLabel.setFont(labelFont);  // Cambiar fuente
        formPanel.add(salarioLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(salarioField, colocar);

        colocar.gridx = 2; colocar.gridy = 1;
        JLabel imagenLabel = new JLabel("Imagen (URL):");
        imagenLabel.setFont(labelFont);
        formPanel.add(imagenLabel, colocar);
        colocar.gridx = 3;
        formPanel.add(imagenField, colocar);


        colocar.gridx = 0; colocar.gridy = 2;
        JLabel detallesLabel = new JLabel("Detalles:");
        detallesLabel.setFont(labelFont);
        formPanel.add(detallesLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(detallesField, colocar);

        // Botón Publicar
        JButton submitButton = new JButton("Publicar");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));  // Aumentar tamaño de fuente
        submitButton.setPreferredSize(new Dimension(150, 40));  // Hacer el botón más pequeño
        submitButton.addActionListener(e -> {
            try {
                String salarioText = salarioField.getText().trim().replace(",", ".");
                double salario = Double.parseDouble(salarioText);

                Vacante vacante = new Vacante();
                vacante.setNombre(nombreField.getText());
                vacante.setDescripcion(descripcionField.getText());
                vacante.setSalario(salario);
                vacante.setImagen(imagenField.getText());
                vacante.setDetalles(detallesField.getText());

                boolean exito = apiClient.crearVacante(vacante);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "Vacante creada con éxito");
                    mostrarVacantes();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al publicar la vacante");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en el formato del salario");
            }
        });

        colocar.gridx = 1; colocar.gridy = 3;
        colocar.gridwidth = 2;  // El botón ocupará toda la línea
        colocar.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, colocar);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();

    }

    private void modificarVacante(){

        contentPanel.removeAll();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints colocar = new GridBagConstraints();
        colocar.insets = new Insets(20, 20, 20, 20);
        colocar.fill = GridBagConstraints.HORIZONTAL;

        JTextField idVacanteField = new JTextField(25);
        JTextField nombreField = new JTextField(25);
        JTextField descripcionField = new JTextField(25);
        JTextField salarioField = new JTextField(25);
        JTextField imagenField = new JTextField(25);
        JTextField detallesField = new JTextField(25);

        Font labelFont = new Font("Arial", Font.BOLD, 18);


        colocar.gridx = 0; colocar.gridy = 0;
        JLabel idVacanteLabel = new JLabel("ID Vacante:");
        idVacanteLabel.setFont(labelFont);
        formPanel.add(idVacanteLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(idVacanteField, colocar);


        colocar.gridx = 0; colocar.gridy = 1;
        JLabel nombreLabel = new JLabel("Nombre:");
        nombreLabel.setFont(labelFont);
        formPanel.add(nombreLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(nombreField, colocar);


        colocar.gridx = 2; colocar.gridy = 1;
        JLabel descripcionLabel = new JLabel("Descripción:");
        descripcionLabel.setFont(labelFont);
        formPanel.add(descripcionLabel, colocar);
        colocar.gridx = 3;
        formPanel.add(descripcionField, colocar);


        colocar.gridx = 0; colocar.gridy = 2;
        JLabel salarioLabel = new JLabel("Salario:");
        salarioLabel.setFont(labelFont);
        formPanel.add(salarioLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(salarioField, colocar);


        colocar.gridx = 2; colocar.gridy = 2;
        JLabel imagenLabel = new JLabel("Imagen (URL):");
        imagenLabel.setFont(labelFont);
        formPanel.add(imagenLabel, colocar);
        colocar.gridx = 3;
        formPanel.add(imagenField, colocar);


        colocar.gridx = 0; colocar.gridy = 3;
        JLabel detallesLabel = new JLabel("Detalles:");
        detallesLabel.setFont(labelFont);
        formPanel.add(detallesLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(detallesField, colocar);


        JButton submitButton = new JButton("Publicar");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));  // Aumentar tamaño de fuente
        submitButton.setPreferredSize(new Dimension(150, 40));  // Hacer el botón más pequeño
        submitButton.addActionListener(e -> {
            try {
                String salarioText = salarioField.getText().trim().replace(",", ".");
                double salario = Double.parseDouble(salarioText);

                // Obtenemos el idVacante en este campo de texto que pasaremos al ApiClient posteriormente
                int idVacante = Integer.parseInt(idVacanteField.getText().trim());

                Vacante vacante = new Vacante();
                vacante.setNombre(nombreField.getText());
                vacante.setDescripcion(descripcionField.getText());
                vacante.setSalario(salario);
                vacante.setImagen(imagenField.getText());
                vacante.setDetalles(detallesField.getText());

                // Aquí pasamos el idVacante y los datos de vacante
                boolean exito = apiClient.modificarVacante(idVacante, vacante);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "Vacante modificada con éxito");
                    mostrarVacantes();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al modificar la vacante");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en el formato del salario o ID de vacante");
            }
        });

        colocar.gridx = 1; colocar.gridy = 4;
        colocar.gridwidth = 2;  // El botón ocupará toda la línea
        colocar.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, colocar);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void cancelarVacante(){

        contentPanel.removeAll();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints colocar = new GridBagConstraints();
        colocar.insets = new Insets(20, 20, 20, 20);
        colocar.fill = GridBagConstraints.HORIZONTAL;

        JTextField idVacanteField = new JTextField(25);

        Font labelFont = new Font("Arial", Font.BOLD, 18);


        colocar.gridx = 0; colocar.gridy = 0;
        JLabel idVacanteLabel = new JLabel("ID Vacante:");
        idVacanteLabel.setFont(labelFont);
        formPanel.add(idVacanteLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(idVacanteField, colocar);


        JButton submitButton = new JButton("Cancelar Vacante");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));  // Aumentar tamaño de fuente
        submitButton.setPreferredSize(new Dimension(150, 40));  // Hacer el botón más pequeño
        submitButton.addActionListener(e -> {
            try {
                // Obtenemos el id de la vacante
                int idVacante = Integer.parseInt(idVacanteField.getText().trim());

                //Llamamos a apiClient y le pasamos el idVacante a nuestro metodo cancelarVacante
                boolean exito = apiClient.cancelarVacante(idVacante);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "La vacante ha sido cancelada con éxito");
                    mostrarVacantes();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al cancelar la vacante");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en el formato del ID de vacante");
            }
        });

        colocar.gridx = 1; colocar.gridy = 1;
        colocar.gridwidth = 2;
        colocar.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, colocar);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void asignarVacante(){
        contentPanel.removeAll();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints colocar = new GridBagConstraints();
        colocar.insets = new Insets(20, 20, 20, 20);
        colocar.fill = GridBagConstraints.HORIZONTAL;

        JTextField idVacanteField = new JTextField(25);
        JTextField idSolicitudField = new JTextField(25);

        Font labelFont = new Font("Arial", Font.BOLD, 18);


        colocar.gridx = 0; colocar.gridy = 0;
        JLabel idVacanteLabel = new JLabel("ID Vacante:");
        idVacanteLabel.setFont(labelFont);
        formPanel.add(idVacanteLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(idVacanteField, colocar);


        colocar.gridx = 0; colocar.gridy = 1;
        JLabel idSolicitudLabel = new JLabel("ID Solicitud:");
        idSolicitudLabel.setFont(labelFont);
        formPanel.add(idSolicitudLabel, colocar);
        colocar.gridx = 1;
        formPanel.add(idSolicitudField, colocar);


        JButton submitButton = new JButton("Asignar Vacante");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));  // Aumentar tamaño de fuente
        submitButton.setPreferredSize(new Dimension(150, 40));  // Hacer el botón más pequeño
        submitButton.addActionListener(e -> {
            try {
                // Obtenemos los ids de los campos de texto
                int idVacante = Integer.parseInt(idVacanteField.getText().trim());
                int idSolicitud = Integer.parseInt(idSolicitudField.getText().trim());

                // Hacemos una llamada a nuestro apiclient y le pasamos nuestros ids a asignarVacante()
                boolean exito = apiClient.asignarVacante(idVacante, idSolicitud);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "La vacante ha sido asignada correctamente");
                    mostrarVacantes();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al asignar la vacante");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en el formato de los IDs");
            }
        });

        colocar.gridx = 1; colocar.gridy = 2;
        colocar.gridwidth = 2;
        colocar.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, colocar);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void mostrarSolicitudes(int idVacante){

        List<Solicitud> solicitudes = apiClient.obtenerSolicitudes(idVacante);

        if (solicitudes != null && !solicitudes.isEmpty()) {
            // Crear la tabla y sus columnas
            String[] columnNames = { "Archivo", "Comentario", "Estado", "Fecha"};
            Object[][] data = new Object[solicitudes.size()][5];

            for (int i = 0; i < solicitudes.size(); i++) {
                Solicitud solicitud = solicitudes.get(i);
                data[i][1] = solicitud.getArchivo();
                data[i][2] = solicitud.getComentario();


                //Si la solicitud contiene fecha la formateamos sino pondremos fecha no disponible así evitamos el null
                if (solicitud.getFecha() != null) {
                    data[i][4] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(solicitud.getFecha());
                } else {
                    data[i][4] = "Fecha no disponible";
                }

                if(solicitud.getEstado() == 0){
                    data[i][3] = "En espera";

                }else {
                    data[i][3] = "Adjudicada";
                }
            }

            //Creamos la tabla e insertamos todos los datos
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            // Mostrar la tabla en el panel
            contentPanel.removeAll();
            contentPanel.setLayout(new BorderLayout());
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontraron solicitudes para esta vacante.");
        }

    }
    private void mostrarFormulario() {

        contentPanel.removeAll();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JTextField idVacanteField = new JTextField(25);
        Font labelFont = new Font("Arial", Font.BOLD, 18);


        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idVacanteLabel = new JLabel("ID Vacante:");
        idVacanteLabel.setFont(labelFont);
        formPanel.add(idVacanteLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idVacanteField, gbc);


        JButton submitButton = new JButton("Obtener Solicitudes");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        submitButton.setPreferredSize(new Dimension(200, 40));
        submitButton.addActionListener(e -> {
            try {
                //Aqui llamamos al metodo mostrarSolicitudes() si todo va bien para que nos pinte la tabla
                int idVacante = Integer.parseInt(idVacanteField.getText().trim());
                mostrarSolicitudes(idVacante);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido para el ID de la vacante.");
            }
        });


        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(submitButton, gbc);


        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}

