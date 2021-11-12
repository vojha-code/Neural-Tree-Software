/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdditionalFrames;


import FNT.FNT;
import Randoms.MersenneTwisterFast;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author ojh0009
 */
public class IndividualSelector extends javax.swing.JFrame {

    /**
     * Creates new form individualSelector
     */
    DefaultTableModel model;
    public int[] returnSele;
    public boolean isSubmited = false;
    private int totalSelection;

    public IndividualSelector() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabelDisplayNumber = new javax.swing.JLabel();
        jLabelDisplayNumber2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Individuals ");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Appropriate Individual"));

        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabelDisplayNumber.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelDisplayNumber.setForeground(new java.awt.Color(255, 0, 0));
        jLabelDisplayNumber.setText("8");

        jLabelDisplayNumber2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelDisplayNumber2.setForeground(new java.awt.Color(255, 0, 0));
        jLabelDisplayNumber2.setText("Are you Sure? This is single chance");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Individual ", "Fitness", "Size", "Parameters", "Function Nodes", "Leaf Nodes", "Diversity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        jLabel1.setText("Press Ctrl + Click and select total ");

        jLabel2.setText("indivuals ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDisplayNumber2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 831, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelDisplayNumber)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)))
                        .addGap(0, 5, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDisplayNumber)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabelDisplayNumber2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        MersenneTwisterFast m_Random = new MersenneTwisterFast();
        int columnCount = model.getColumnCount();
        int rowCount = model.getRowCount();
        System.out.println("Check error count " + columnCount);
        int indexFill = 0;
        int count = jTable1.getSelectedRowCount();
        if (count != 0) {
            int[] selected = jTable1.getSelectedRows();
            for (int i = 0; i < count; i++) {
                if (indexFill < totalSelection) {
                    //returnSele[indexFill++] = selected[i];
                    returnSele[indexFill] = (int)(jTable1.getValueAt(selected[i], 0));
                    //returnSele[indexFill++] = Integer.parseInt(jTable1.getValueAt(selected[i], 0).toString());
                    System.out.println("Ind(S):" + returnSele[indexFill]);
                    indexFill++;
                } else {
                    break;
                }//selection is more than requared
            }//for
            for(;indexFill < totalSelection;) {                
                returnSele[indexFill] = m_Random.nextInt(columnCount);
                System.out.println("Ind(R):" + returnSele[indexFill]);
                indexFill++;
            }//for
        } else {
            for (int i = 0; i < totalSelection; i++) {
                if (returnSele[i] == -1 && i == 0) {
                    returnSele[i] = 0;
                } else if (returnSele[i] == -1) {
                    returnSele[i] = m_Random.nextInt(columnCount);
                }//if
            }//for
        }//if
        System.out.println("Check error");
        isSubmited = true;
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IndividualSelector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IndividualSelector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IndividualSelector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IndividualSelector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IndividualSelector().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelDisplayNumber;
    private javax.swing.JLabel jLabelDisplayNumber2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    public void setTable(FNT[] mainPopulation, int m_Ensemble_Candidates) {
        model = (DefaultTableModel) jTable1.getModel();
        int N = mainPopulation.length;
        jLabelDisplayNumber.setText(""+ m_Ensemble_Candidates + "");
        totalSelection = m_Ensemble_Candidates;
        returnSele = new int[totalSelection];
        for (int i = 0; i < totalSelection; i++) {
            returnSele[i] = -1;
        }//for
        try {
            for (int i = 0; i < N; i++) {
                int c1 = i;
                double c2 = mainPopulation[i].getFitness();
                int c3 = mainPopulation[i].getSize();
                int c4 = mainPopulation[i].getParametersCount();
                int c5 = mainPopulation[i].getFunctionChild().size();
                int c6 = mainPopulation[i].getLeafChild().size();
                int c7 = mainPopulation[i].getDiversity()*(-1);
                Object[] obj = {c1, c2, c3, c4, c5, c6, c7};
                model.addRow(obj);
            }
            TableRowSorter<DefaultTableModel>sorter = new TableRowSorter<>(model);
            jTable1.setRowSorter(sorter);
            //System.out.print("In Inisiator Finish");
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }
}
