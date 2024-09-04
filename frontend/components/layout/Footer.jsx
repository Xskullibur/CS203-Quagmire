// components/Footer.jsx
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTwitter, faGithub, faSlack, faYoutube } from '@fortawesome/free-brands-svg-icons'
import { Button } from '../ui/button';

export default function Footer() {
  return (
    <footer className="bg-[#000000] text-white py-16">
      <div className="container mx-auto px-4">
        <div className="border-t border-[#ffffff1a] pt-16">
          <div className="grid grid-cols-3 gap-8">
            <div>
              <p className="font-semibold mb-4">Hand Hathaway</p>
              {/* Social Icons */}
              <div className="flex space-x-4 mt-4">
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faTwitter} className="w-5 h-5" />
                </Button>
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faGithub} className="w-5 h-5" />
                </Button>
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faSlack} className="w-5 h-5" />
                </Button>
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faYoutube} className="w-5 h-5" />
                </Button>
              </div>
            </div>
            <div>
              <h3 className="font-semibold mb-4">Product</h3>
              <ul className="space-y-2">
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Features</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Integrations</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Changelog</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Docs</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Downloads</a></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4">Company</h3>
              <ul className="space-y-2">
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">About us</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Blog</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Careers</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Customers</a></li>
                <li><a href="#" className="text-[#ffffffb3] hover:text-white">Brand</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}