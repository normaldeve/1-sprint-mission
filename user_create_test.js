import http from 'k6/http';
import { check } from 'k6';

export let options = {
  vus: 5,
  duration: '5s',
};

// 전역에서 파일 로딩 (init stage)
const sampleImage = open('./sample.jpg', 'b');

export default function () {
  const url = 'http://localhost:8080/api/users';

  const json = JSON.stringify({
    username: `testuser_${Math.random().toString(36).substring(7)}`,
    password: 'test1234!',
    nickname: 'tester',
    email: `user${Math.floor(Math.random() * 100000)}@test.com`,
  });

  const formData = {
    userCreateRequest: http.file(json, 'user.json', 'application/json'),
    profile: http.file(sampleImage, 'sample.jpg', 'image/jpeg'),
  };

  const res = http.post(url, formData);

  console.log(`Response: ${res.status}`);
  check(res, { 'status is 201': (r) => r.status === 201 });
}
