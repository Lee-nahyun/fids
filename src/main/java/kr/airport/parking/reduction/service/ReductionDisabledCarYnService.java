package kr.airport.parking.reduction.service;

import kr.airport.parking.reduction.vo.table.AdminInfomationParkingCar;
import kr.airport.parking.reduction.vo.table.RealTimeParkingCar;

public interface ReductionDisabledCarYnService {

	public AdminInfomationParkingCar proc(RealTimeParkingCar realTimeParkingCar,Boolean dbProc,AdminInfomationParkingCar beforeAdminInfomationParkingCar);
}

