/** @type {import('next').NextConfig} */
const nextConfig = {
  // Required for the Docker standalone build used in production
  output: 'standalone',
};

export default nextConfig;
