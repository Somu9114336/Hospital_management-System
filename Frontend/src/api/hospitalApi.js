import { api } from './client.js';

export const hospitalApi = {
  register: (request) => api.post('/user/register', request),
  login: (request) => api.post('/user/login', request),
  getMyAppointments: () => api.get('/appointments/getallmyappointments'),
  getAppointmentById: (id) => api.get(`/appointments/get/${id}`),
  bookAppointment: (request) => api.post('/appointments/book', request),
  rescheduleAppointment: (id, request) => api.put(`/appointments/reshedule/${id}`, request),
  cancelAppointment: (id) => api.patch(`/appointments/cancle/${id}`),
  findDoctors: (name) => api.get(`/doctor/getbyname/${encodeURIComponent(name)}`),
};
