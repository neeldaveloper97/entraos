
export const validDate = (value) => {
    if (typeof value === 'undefined' || value === null || value === '') {
      return true
    }
    return /[\d]{2}\.[\d]{4}/.test(value);
  }

/**
 * checks if number is valid E.164 Format
 */
  export const isValidPhone = (value) => {
  if (typeof value === 'undefined' || value === null || value === '')
    return true;
  return /^\+?[1-9]\d{1,14}$/.test(value);
  }
  export const isUnique = (value, arr) => {
    if (typeof value === 'undefined' || value === null || value === '') {
      return true
    }
    return !arr.includes(value);
  }
