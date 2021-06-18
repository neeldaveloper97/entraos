

export const toBase64 = file => new Promise((resolve, reject) => {
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = () => resolve(reader.result);
  reader.onerror = error => reject(error);
});

export const urltoFile = (url, filename) => fetch(url.includes('quadim') ? url.replace('http', 'https') : url)
  .then(res => res.arrayBuffer())
  .then(buf => new File([buf], filename, { type: 'image/jpeg' }));

