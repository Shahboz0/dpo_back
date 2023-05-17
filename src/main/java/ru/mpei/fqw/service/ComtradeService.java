package ru.mpei.fqw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mpei.fqw.client.FourierImpl;
import ru.mpei.fqw.client.VectorF;
import ru.mpei.fqw.dto.FaultCurrentDto;
import ru.mpei.fqw.model.FaultCurrentModel;
import ru.mpei.fqw.repository.RepositoryIml;
import ru.mpei.fqw.utils.ComtradeToJson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class ComtradeService {
    @Autowired
    private RepositoryIml repository;

    @Value("${cfgFileName}")
    private String cfgFileName;
    @Value("${datFileName}")
    private String datFileName;
    @Value("${current}")
    private String current;

    public List<FaultCurrentModel> getFaultCurrentInfo(){
        return this.repository.getFaultCurrentInfo();
    }
    public String comtradeToJSON(){
        ObjectMapper currentMapper = new ObjectMapper();
        SetCurrent setCurrent = new SetCurrent();
        try {
            setCurrent = currentMapper.readValue(current, SetCurrent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream cfgInputStream = classLoader.getResourceAsStream(cfgFileName);
        InputStream datInputStream = classLoader.getResourceAsStream(datFileName);
        List<CfgInfo> cfgInfoList = readCfgFileFromInputStream(cfgInputStream);
        List<List<Integer>> datData = readDatFileFromInputStream(datInputStream);
        List<ComtradeToJson> comtradeList = new ArrayList<>();

        List<FaultCurrentDto> faultCurrentDtoList = new ArrayList<>();
        for (int i = 0; i < cfgInfoList.size(); i++) {
            ComtradeToJson comtrade = new ComtradeToJson();
            List<Double> values = new ArrayList<>();
            List<Double> rms = new ArrayList<>();
            FourierImpl fourier = new FourierImpl(1);
            VectorF vectorF = new VectorF();

            for(int j = 0; j < datData.get(i).size(); j++) {
                if (cfgInfoList.get(i).getType().equals("analog")) {
                    double val = datData.get(i).get(j) * cfgInfoList.get(i).getK1() + cfgInfoList.get(i).getK2();
                    values.add(val);
                    fourier.process(val, vectorF);
                    rms.add(vectorF.getMag());
                    if (setCurrent.getName().equals(cfgInfoList.get(i).getName()) && setCurrent.getValue() < vectorF.getMag()){
                        faultCurrentDtoList.add(new FaultCurrentDto(setCurrent.getName(), vectorF.getMag(), j));
                    }
                }
            }
            comtrade.setName(cfgInfoList.get(i).getName());
            comtrade.setType(cfgInfoList.get(i).getType());
            comtrade.setValues(cfgInfoList.get(i).getType().equals("analog") ? values : datData.get(i));
            comtrade.setRMS(rms);
            comtradeList.add(comtrade);
        }


        for (int i = 0; i < comtradeList.size(); i++){
            if (comtradeList.get(i).getName().equals("Time")){
                Integer samplingStep = (Integer) comtradeList.get(i).getValues().get(1) - (Integer) comtradeList.get(i).getValues().get(0);
                faultCurrentDtoList.forEach(faultCurrentDto -> {
                    faultCurrentDto.setTime((samplingStep * faultCurrentDto.getIndexOfValues()) / 1000);
                    this.repository.save(new FaultCurrentModel(faultCurrentDto.getName(), faultCurrentDto.getValue(), faultCurrentDto.getTime(), faultCurrentDto.getIndexOfValues()));
                });

            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String dataInJson = null;
        try {
            dataInJson = mapper.writeValueAsString(comtradeList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dataInJson;
    }



    @SneakyThrows
    private List<CfgInfo> readCfgFileFromInputStream(InputStream inputStream) {
        List<CfgInfo> cfgInfoList = new ArrayList<>();
        cfgInfoList.add(new CfgInfo("Time", null, null, "time"));
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            val a = StringUtils.split(line, ",");
            if (a.length > 3){
                CfgInfo cfgInfo = new CfgInfo();
                cfgInfo.setName(a[1]);
                if (StringUtils.contains(line, ",P")){
                    cfgInfo.setType("analog");
                    cfgInfo.setK1(Double.parseDouble(a[3]));
                    cfgInfo.setK2(Double.parseDouble(a[4]));
                }
                else {
                    cfgInfo.setType("discrete");
                }
                cfgInfoList.add(cfgInfo);
            }
        }
        return cfgInfoList;
    }

    @SneakyThrows
    private List<List<Integer>> readDatFileFromInputStream(InputStream inputStream){
        List<List<Integer>> listValues = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            val b = Arrays.stream(Arrays.stream(StringUtils.split(line, ',')).mapToInt(Integer::parseInt).toArray()).boxed().collect(Collectors.toList());
            b.remove(0);
            listValues.add(b);
        }
        return transpose(listValues);
    }

    private <T> List<List<T>> transpose(List<List<T>> matrix) {
        List<List<T>> transposedMatrix = new ArrayList<>();

        int nbOfColumns = -1;
        int nbOfRows = -1;

        // Check if the given list of lists represents a proper matrix, i.e., each row has to feature an equal number of
        // columns
        if (matrix != null && !matrix.isEmpty()) {
            nbOfRows = matrix.size();

            for (List<T> row : matrix) {
                if (nbOfColumns == -1)
                    nbOfColumns = row.size();
                else if (nbOfColumns != row.size())
                    throw new IllegalArgumentException("The given list of lists is not a proper matrix.");
            }
        }

        // transpose the matrix
        for (int i = 0; i < nbOfColumns; i++) {
            List<T> newRow = new ArrayList<T>(nbOfRows);

            for (List<T> row : matrix) {
                newRow.add(row.get(i));
            }

            transposedMatrix.add(newRow);
        }

        return transposedMatrix;
    }


}
@Data
@AllArgsConstructor
@NoArgsConstructor
class CfgInfo{
    private String name;
    private Double k1;
    private Double k2;
    private String type;
}
@Data
class SetCurrent{
    private String name;
    private Double value;
}

