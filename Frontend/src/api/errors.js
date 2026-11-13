const statusMessages = {
  400: 'Please check the information and try again.',
  401: 'Your session has expired. Please sign in again.',
  403: 'You do not have permission to do this. The backend may not allow this patient action yet.',
  404: 'We could not find what you requested.',
  500: 'The hospital server encountered an error. Please try again later.',
};

export function getErrorMessage(error) {
  const status = error.response?.status;
  const data = error.response?.data;
  if (typeof data === 'string' && data.trim()) return data;
  if (data?.error) return data.error;
  if (data?.message) return data.message;
  if (statusMessages[status]) return statusMessages[status];
  if (!error.response && error.isAxiosError) return 'Unable to reach the hospital server. Check your connection and try again.';
  if (!error.response && error.message) return error.message;
  return 'Something went wrong. Please try again.';
}

export function getFieldErrors(error) {
  const data = error.response?.data;
  const errors = {};
  const add = (field, message) => {
    if (field && message) errors[field] = message;
  };

  if (Array.isArray(data?.errors)) {
    data.errors.forEach((item) => add(item.field, item.defaultMessage || item.message));
  } else if (data?.errors && typeof data.errors === 'object') {
    Object.entries(data.errors).forEach(([field, value]) => {
      add(field, Array.isArray(value) ? value[0] : value);
    });
  }
  if (Array.isArray(data?.detail)) {
    data.detail.forEach((item) => add(item.field, item.message));
  }
  return errors;
}
