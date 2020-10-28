package com.rodolfo.ulcer.segmentation.core.descriptors.texture;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;

public class HaralickDescriptors {
    
    private Mat glcm;

    public HaralickDescriptors(Mat hGlcm) {

        this.glcm = hGlcm;
    }

    /**
     * Cálculo do contraste ou inércia (Contrast or Ineria)
     * 
     * @return Float
     */
    public Float contrast() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        Double contrast = 0.0;

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                contrast += Math.pow((row - col), 2) * glcmIndex.get(row, col);
            }
        }

        glcmIndex.release();

        return contrast.floatValue();
    }

    /**
     * Cálculo da energia ou segundo momento angular (Energy or angular second moment - ASM)
     * 
     * @return Float
     */
    public Float energy() {

        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        Float asm1     = 0f;

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                asm1 += glcmIndex.get(row, col) * glcmIndex.get(row, col);
            }
        }

        glcmIndex.release();

        Double energia = Math.sqrt(asm1.doubleValue());

        return energia.floatValue();
    }

    /**
     * Cálculo da entropia (Entropy)
     * 
     * @return entropia
     */
    public Float entropy() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        Double entropia = 0.0;

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                if(glcmIndex.get(row, col) != 0f) {

                    entropia += glcmIndex.get(row, col) * ((Math.log(glcmIndex.get(row, col))) / (Math.log(2)));
                }
            }
        }
        
        glcmIndex.release();

        return -entropia.floatValue();
    }

    /**
     * Cálculo da homogeneidade ou homogeneidade local ou momento diferencia inverso (Homogeneity or
     * Local Homogeneity or Inverse Difference Moment - IDM)
     * Métrica complementar à inércia. As probabilidades são ponderadas pela proximidade da diagonal.
     * 
     * @return homogeneidade
     */
    public Float homogeneity() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        Float homogeneidade = 0f;

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                homogeneidade += glcmIndex.get(row, col) / (1f + (Math.abs(row - col) * Math.abs(row - col)));
            }
        }

        glcmIndex.release();

        return homogeneidade;
    }

    /**
     * Cálculo da diferença inversa (Inverse Difference)
     * Válidos apenas para elementos não diagonais. 
     * Métrica com relação bem próxima da homogeneidade.
     * 
     * @param glcm
     * @return diferencaInversa
     */
    public static Float inverseDifference(Mat glcm) {
        
        FloatRawIndexer glcmIndex = glcm.createIndexer();
        Double diferenca = 0.0;

        for(int row = 0; row < glcm.rows(); row++) {
            for(int col = 0; col < glcm.cols(); col++) {

                int temp = row - col == 0 ? 1 : row - col;

                diferenca += glcmIndex.get(row, col) / (Math.abs(temp) * 1f);
            }
        }

        glcmIndex.release();

        return diferenca.floatValue();
    }

    /**
     * Cálculo da correlação (Correlation)
     * 
     * @return correlacao
     */
    public Float correlation() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        Double correlacao = 0.0;

        Float mean_1 = 0f;
        Float mean_2 = 0f;
        Double omega_1 = 0.0;
        Double omega_2 = 0.0;

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                mean_1 += row * glcmIndex.get(row, col);
                mean_2 += col * glcmIndex.get(row, col);
            }
        }

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                omega_1 += glcmIndex.get(row, col) * (row - mean_1) * (row - mean_1);
                omega_2 += glcmIndex.get(row, col) * (col - mean_2) * (col - mean_2);
            }
        }

        omega_1 = Math.sqrt(omega_1);
        omega_2 = Math.sqrt(omega_2);

        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                correlacao += glcmIndex.get(row, col) * (((row - mean_1) * (col - mean_2)) / (Math.sqrt(omega_1 * omega_2))) ;
            }
        }

        glcmIndex.release();

        return correlacao.floatValue();
    }

    /**
     * Cálculo da tendência de cluster (Cluster Tendency)
     * As probabilidades são ponderadas pelo seu desvio dos valores médios.
     * 
     * @return tendencia cluster
     */
    public Float clusterTendency() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        
        Float[] meanRow = new Float[this.glcm.rows()];
        Float[] meanCol = new Float[this.glcm.cols()];

        for (int i = 0; i < this.glcm.rows(); i++) {
            
            meanRow[i] = 0f;
        }

        for (int i = 0; i < this.glcm.cols(); i++) {
            
            meanCol[i] = 0f;
        }
        
        Double tendencia = 0.0;

        for(int i = 0; i < meanRow.length; i++) {
            for(int j = 0; j < this.glcm.rows(); j++) {

                meanRow[i] += glcmIndex.get(i, j);
            }

            meanRow[i] /= glcmIndex.rows();
        }

        for(int j = 0; j < meanCol.length; j++) {
            for(int i = 0; i < this.glcm.rows(); i++) {

                meanCol[i] += glcmIndex.get(i, j);
            }

            meanCol[j] /= glcmIndex.cols();
        }


        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                tendencia += Math.pow((row - meanRow[row]) + (col - meanCol[col]), 2) * glcmIndex.get(row, col);
            }
        }

        glcmIndex.release();

        return tendencia.floatValue();
    }

    /**
     * Cálculo da matiz de cluster (Cluster Shade)
     * 
     * @return matiz cluster
     */
    public Float clusterShade() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        
        Float[] meanRow = new Float[this.glcm.rows()];
        Float[] meanCol = new Float[this.glcm.cols()];

        for (int i = 0; i < this.glcm.rows(); i++) {
            
            meanRow[i] = 0f;
        }

        for (int i = 0; i < this.glcm.cols(); i++) {
            
            meanCol[i] = 0f;
        }
        
        Double matiz = 0.0;

        for(int i = 0; i < meanRow.length; i++) {
            for(int j = 0; j < this.glcm.rows(); j++) {

                meanRow[i] += glcmIndex.get(i, j);
            }

            meanRow[i] /= glcmIndex.rows();
        }

        for(int j = 0; j < meanCol.length; j++) {
            for(int i = 0; i < this.glcm.rows(); i++) {

                meanCol[i] += glcmIndex.get(i, j);
            }

            meanCol[j] /= glcmIndex.cols();
        }


        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                matiz += Math.pow((row - meanRow[row]) + (col - meanCol[col]), 3) * glcmIndex.get(row, col);
            }
        }

        glcmIndex.release();

        return matiz.floatValue();
    }

    /**
     * Cálculo da saliência/notaridade (Cluster Prominence)
     * 
     * @return saliencia cluster
     */
    public Float clusterProminence() {
        
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();
        
        Float[] meanRow = new Float[this.glcm.rows()];
        Float[] meanCol = new Float[this.glcm.cols()];

        for (int i = 0; i < this.glcm.rows(); i++) {
            
            meanRow[i] = 0f;
        }

        for (int i = 0; i < this.glcm.cols(); i++) {
            
            meanCol[i] = 0f;
        }
        
        Double saliencia = 0.0;

        for(int i = 0; i < meanRow.length; i++) {
            for(int j = 0; j < this.glcm.rows(); j++) {

                meanRow[i] += glcmIndex.get(i, j);
            }

            meanRow[i] /= glcmIndex.rows();
        }

        for(int j = 0; j < meanCol.length; j++) {
            for(int i = 0; i < this.glcm.rows(); i++) {

                meanCol[i] += glcmIndex.get(i, j);
            }

            meanCol[j] /= glcmIndex.cols();
        }


        for(int row = 0; row < this.glcm.rows(); row++) {
            for(int col = 0; col < this.glcm.cols(); col++) {

                saliencia += Math.pow((row - meanRow[row]) + (col - meanCol[col]), 4) * glcmIndex.get(row, col);
            }
        }

        glcmIndex.release();

        return saliencia.floatValue();
    }

}