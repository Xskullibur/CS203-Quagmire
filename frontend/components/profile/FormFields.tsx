// components/profile/FormFields.tsx
import React from 'react';
import { PlayerProfile } from '@/types/player-profile';

interface FormFieldsProps {
  profileData: PlayerProfile;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
}

const FormFields: React.FC<FormFieldsProps> = ({ profileData, onChange }) => (
  <div className="space-y-4">
    <div>
      <label htmlFor="firstName" className="block text-xl mb-2">
        First Name:
      </label>
      <input
        type="text"
        id="firstName"
        name="firstName"
        value={profileData.firstName || ""}
        onChange={onChange}
        className="w-full p-2 rounded-lg bg-[#333333] text-white"
      />
    </div>
    
    <div>
      <label htmlFor="lastName" className="block text-xl mb-2">
        Last Name:
      </label>
      <input
        type="text"
        id="lastName"
        name="lastName"
        value={profileData.lastName || ""}
        onChange={onChange}
        className="w-full p-2 rounded-lg bg-[#333333] text-white"
      />
    </div>
    
    <div>
      <label htmlFor="bio" className="block text-xl mb-2">
        Bio:
      </label>
      <textarea
        id="bio"
        name="bio"
        value={profileData.bio || ""}
        onChange={onChange}
        className="w-full p-2 rounded-lg bg-[#333333] text-white"
      />
    </div>
    
    <div>
      <label htmlFor="country" className="block text-xl mb-2">
        Country:
      </label>
      <input
        type="text"
        id="country"
        name="country"
        value={profileData.country || ""}
        onChange={onChange}
        className="w-full p-2 rounded-lg bg-[#333333] text-white"
      />
    </div>
    
    <div>
      <label htmlFor="dateOfBirth" className="block text-xl mb-2">
        Date of Birth:
      </label>
      <input
        type="date"
        id="dateOfBirth"
        name="dateOfBirth"
        value={profileData.dateOfBirth || ""}
        onChange={onChange}
        className="w-full p-2 rounded-lg bg-[#333333] text-white"
      />
    </div>
  </div>
);

export default FormFields;