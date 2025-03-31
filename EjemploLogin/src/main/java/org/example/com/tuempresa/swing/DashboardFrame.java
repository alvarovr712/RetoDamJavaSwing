package org.example.com.tuempresa.swing;

import org.example.com.tuempresa.swing.model.Vacante;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;
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
        JButton botonVerVacante = new JButton("Ver una Vacante");
        JButton botonVerTodasVacantes = new JButton("Ver todas las vacantes");
        botonVerTodasVacantes.addActionListener(e ->mostrarVacantes());

        JButton botonEliminarVacante = new JButton("Eliminar vacante");

        //Agregamos nuestros botones al menú

        menuPanel.add(botonPublicarVacante);
        menuPanel.add(botonEditarVacante);
        menuPanel.add(botonVerVacante);
        menuPanel.add(botonVerTodasVacantes);
        menuPanel.add(botonEliminarVacante);

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
        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JTextField salarioField = new JTextField();
        JTextField imagenField = new JTextField();
        JTextField detallesField = new JTextField();


        Object[] fields = {
                "Nombre:", nombreField,
                "Descripción:", descripcionField,
                "Salario:", salarioField,
                "Imagen (URL):", imagenField,
                "Detalles:", detallesField
        };

        int resultado = JOptionPane.showConfirmDialog(null, fields, "Publicar Vacante", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String salarioText = salarioField
                        .getText()
                        .trim()
                        .replace(",", ".");

                double salario = Double.parseDouble(salarioText);

                Vacante vacante = new Vacante();

                vacante.setNombre(nombreField.getText());
                vacante.setDescripcion(descripcionField.getText());
                vacante.setSalario(salario);
                vacante.setImagen(imagenField.getText());
                vacante.setDetalles(detallesField.getText());

                System.out.println(vacante.toString());

                boolean exito = apiClient.crearVacante(vacante);

                if (exito) {
                    JOptionPane.showMessageDialog(null, "Vacante creada con éxito");
                    mostrarVacantes();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al publicar la vacante");
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

